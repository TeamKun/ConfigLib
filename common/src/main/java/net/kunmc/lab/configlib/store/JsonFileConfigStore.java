package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.JsonMigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class JsonFileConfigStore implements ConfigStore {
    private final File file;
    private final Gson gson;
    private final Consumer<Exception> exceptionHandler;
    private final int maxHistorySize;

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
        int storedVersion = jsonObject.has("_version_") ? jsonObject.get("_version_")
                                                                    .getAsInt() : 0;
        if (migrations.apply(storedVersion, new JsonMigrationContext(gson, jsonObject))) {
            jsonObject.addProperty("_version_", migrations.latestVersion());
            writeString(file, gson.toJson(jsonObject));
        }
        return gson.fromJson(jsonObject, clazz);
    }

    @Override
    public void write(CommonBaseConfig config) {
        try {
            file.getParentFile()
                .mkdirs();
            file.createNewFile();
            writeString(file, gson.toJson(config));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        writeString(hf, gson.toJson(reordered));
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
        writeString(hf, gson.toJson(remaining));

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
                                    .register(watchService, ENTRY_MODIFY);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path changed = file.getParentFile()
                                           .toPath()
                                           .resolve((Path) e.context());
                        if (changed.equals(file.toPath())) {
                            try {
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
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeString(File file, String content) {
        try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()),
                                                                StandardCharsets.UTF_8)) {
            writer.write(content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
