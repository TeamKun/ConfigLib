package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.JsonMigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class JsonFileConfigStore implements ConfigStore {
    private final File file;
    private final Gson gson;
    private final Consumer<Exception> exceptionHandler;

    public JsonFileConfigStore(File file, Gson gson) {
        this(file, gson, Throwable::printStackTrace);
    }

    public JsonFileConfigStore(File file, Gson gson, Consumer<Exception> exceptionHandler) {
        this.file = file;
        this.gson = gson;
        this.exceptionHandler = exceptionHandler;
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
    public Closeable startWatching(Timer timer, Runnable onChanged) {
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
            timer.scheduleAtFixedRate(task, 0, 500);

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
