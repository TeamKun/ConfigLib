package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.ConfigKeys;
import net.kunmc.lab.configlib.migration.JsonMigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class JsonFileConfigStore implements ConfigStore {
    private final File file;
    private final Gson gson;
    private final Consumer<Exception> exceptionHandler;
    private final int maxHistorySize;
    private final Logger logger = Logger.getLogger(JsonFileConfigStore.class.getName());
    private JsonObject lastLoadedSnapshot;
    private JsonObject lastWrittenSnapshot;

    public JsonFileConfigStore(File file, Gson gson) {
        this(file, gson, Throwable::printStackTrace, 50);
    }

    public JsonFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler) {
        this(file, gson, exceptionHandler, 50);
    }

    public JsonFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler, int maxHistorySize) {
        this.file = file;
        this.gson = gson;
        this.exceptionHandler = exceptionHandler;
        this.maxHistorySize = maxHistorySize;
    }

    public File file() {
        return file;
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public CommonBaseConfig read(Class<? extends CommonBaseConfig> clazz, Migrations migrations) {
        JsonObject jsonObject = JsonParser.parseString(readString(file))
                                          .getAsJsonObject();
        int storedVersion = jsonObject.has(ConfigKeys.VERSION) ? jsonObject.get(ConfigKeys.VERSION)
                                                                           .getAsInt() : 0;
        if (migrations.apply(storedVersion, new JsonMigrationContext(gson, jsonObject))) {
            jsonObject.addProperty(ConfigKeys.VERSION, migrations.latestVersion());
            writeStringAtomically(file, gson.toJson(jsonObject));
        }
        lastLoadedSnapshot = jsonObject.deepCopy();
        return gson.fromJson(jsonObject, clazz);
    }

    @Override
    public CommonBaseConfig write(CommonBaseConfig config,
                                  Class<? extends CommonBaseConfig> clazz,
                                  Migrations migrations) {
        file.getParentFile()
            .mkdirs();
        JsonObject memory = JsonParser.parseString(gson.toJson(config))
                                      .getAsJsonObject();
        JsonObject merged = memory;
        if (file.exists()) {
            JsonObject disk = JsonParser.parseString(readString(file))
                                        .getAsJsonObject();
            int storedVersion = disk.has(ConfigKeys.VERSION) ? disk.get(ConfigKeys.VERSION)
                                                                   .getAsInt() : 0;
            if (migrations.apply(storedVersion, new JsonMigrationContext(gson, disk))) {
                disk.addProperty(ConfigKeys.VERSION, migrations.latestVersion());
            }
            merged = mergeWithDiskPriority(lastLoadedSnapshot, memory, disk);
        }
        String json = gson.toJson(merged);
        writeStringAtomically(file, json);
        lastLoadedSnapshot = merged.deepCopy();
        lastWrittenSnapshot = merged.deepCopy();
        return gson.fromJson(merged, clazz);
    }

    public void write(CommonBaseConfig config) {
        write(config, config.getClass(), new Migrations(new java.util.TreeMap<>()));
    }

    @Override
    public void pushHistory(CommonBaseConfig config) {
        File hf = historyFile();
        JsonArray array = hf.exists() ? JsonParser.parseString(readString(hf))
                                                  .getAsJsonArray() : new JsonArray();
        JsonObject newEntry = JsonParser.parseString(gson.toJson(config))
                                        .getAsJsonObject();
        newEntry.addProperty("_ts_", System.currentTimeMillis());
        // keep most recent at index 0 — insert at front by rebuilding
        JsonArray reordered = new JsonArray();
        reordered.add(newEntry);
        for (int i = 0; i < array.size() && i < maxHistorySize - 1; i++) {
            reordered.add(array.get(i));
        }
        try {
            hf.getParentFile()
              .mkdirs();
            hf.createNewFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        writeStringAtomically(hf, gson.toJson(reordered));
    }

    @Override
    public boolean canUndo(int stepsBack) {
        File hf = historyFile();
        if (!hf.exists()) {
            return false;
        }
        JsonArray array = JsonParser.parseString(readString(hf))
                                    .getAsJsonArray();
        return array.size() >= stepsBack + 1;
    }

    @Override
    public CommonBaseConfig undo(Class<? extends CommonBaseConfig> clazz, Migrations migrations, int stepsBack) {
        File hf = historyFile();
        JsonArray array = JsonParser.parseString(readString(hf))
                                    .getAsJsonArray();

        // 上位 stepsBack エントリを捨て、新しい先頭を現在の状態とする
        JsonObject snapshot = array.get(stepsBack)
                                   .getAsJsonObject();

        JsonArray remaining = new JsonArray();
        for (int i = stepsBack; i < array.size(); i++) {
            remaining.add(array.get(i));
        }
        writeStringAtomically(hf, gson.toJson(remaining));

        return gson.fromJson(snapshot, clazz);
    }

    @Override
    public List<HistoryEntry> readHistory(Class<? extends CommonBaseConfig> clazz, Migrations migrations) {
        File hf = historyFile();
        if (!hf.exists()) {
            return new ArrayList<>();
        }
        JsonArray array = JsonParser.parseString(readString(hf))
                                    .getAsJsonArray();
        List<HistoryEntry> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i)
                                  .getAsJsonObject();
            long ts = obj.has("_ts_") ? obj.get("_ts_")
                                           .getAsLong() : 0L;
            result.add(new HistoryEntry(ts, gson.fromJson(obj, clazz)));
        }
        return result;
    }

    private File historyFile() {
        String name = file.getName();
        String base = name.endsWith(".json") ? name.substring(0, name.length() - 5) : name;
        return new File(file.getParentFile(), base + ".history.json");
    }

    @Override
    public Closeable startWatching(Timer timer, Runnable onChanged, int periodMs) {
        try {
            file.getParentFile()
                .mkdirs();
            WatchService watchService = FileSystems.getDefault()
                                                   .newWatchService();
            WatchKey watchKey = file.getParentFile()
                                    .toPath()
                                    .register(watchService, ENTRY_MODIFY, ENTRY_CREATE);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path changed = file.getParentFile()
                                           .toPath()
                                           .resolve((Path) e.context());
                        if (changed.equals(file.toPath())) {
                            try {
                                if (isLastWrittenSnapshotOnDisk()) {
                                    continue;
                                }
                                onChanged.run();
                            } catch (Exception ex) {
                                exceptionHandler.accept(ex);
                            }
                        }
                    }
                    watchKey.reset();
                }
            };
            timer.scheduleAtFixedRate(task, 0, periodMs);

            return () -> {
                task.cancel();
                watchKey.cancel();
                try {
                    watchService.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            };
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String readString(File file) {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private JsonObject mergeWithDiskPriority(JsonObject base, JsonObject memory, JsonObject disk) {
        if (base == null) {
            return memory.deepCopy();
        }

        JsonObject merged = new JsonObject();
        Set<String> keys = new HashSet<>();
        keys.addAll(base.keySet());
        keys.addAll(memory.keySet());
        keys.addAll(disk.keySet());

        for (String key : keys) {
            JsonElementState baseValue = JsonElementState.of(base, key);
            JsonElementState memoryValue = JsonElementState.of(memory, key);
            JsonElementState diskValue = JsonElementState.of(disk, key);

            JsonElementState selected;
            if (baseValue.equals(memoryValue)) {
                selected = diskValue;
            } else if (baseValue.equals(diskValue)) {
                selected = memoryValue;
            } else if (memoryValue.equals(diskValue)) {
                selected = memoryValue;
            } else {
                logger.warning("Config field \"" + key + "\" was changed both in memory and on disk. Disk value wins. " + "discarded memory value: " + truncate(
                        gson.toJson(memoryValue.element)) + ", disk value: " + truncate(gson.toJson(diskValue.element)));
                selected = diskValue;
            }

            if (selected.present) {
                merged.add(key, selected.element.deepCopy());
            }
        }
        return merged;
    }

    private boolean isLastWrittenSnapshotOnDisk() {
        if (lastWrittenSnapshot == null || !file.exists()) {
            return false;
        }
        try {
            JsonObject disk = JsonParser.parseString(readString(file))
                                        .getAsJsonObject();
            return disk.equals(lastWrittenSnapshot);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static String truncate(String value) {
        if (value == null) {
            return "null";
        }
        return value.length() <= 300 ? value : value.substring(0, 300) + "...";
    }

    private static void writeStringAtomically(File file, String content) {
        try {
            Path path = file.toPath();
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path tmp = Files.createTempFile(parent,
                                            path.getFileName()
                                                .toString(),
                                            ".tmp");
            try {
                Files.writeString(tmp, content, StandardCharsets.UTF_8);
                try {
                    Files.move(tmp, path, ATOMIC_MOVE, REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(tmp, path, REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(tmp);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final class JsonElementState {
        private final boolean present;
        private final com.google.gson.JsonElement element;

        private JsonElementState(boolean present, com.google.gson.JsonElement element) {
            this.present = present;
            this.element = element;
        }

        private static JsonElementState of(JsonObject object, String key) {
            return new JsonElementState(object.has(key), object.get(key));
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JsonElementState)) {
                return false;
            }
            JsonElementState other = (JsonElementState) obj;
            if (present != other.present) {
                return false;
            }
            if (!present) {
                return true;
            }
            return element.equals(other.element);
        }

        @Override
        public int hashCode() {
            return present ? element.hashCode() : 0;
        }
    }
}
