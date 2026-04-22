package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.ConfigKeys;
import net.kunmc.lab.configlib.migration.MigrationExecutionException;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class InMemoryConfigStore implements ConfigStore {
    private final Gson gson;
    private final int maxHistorySize;
    private final transient Logger logger = Logger.getLogger(InMemoryConfigStore.class.getName());
    private String data = null;
    private final Deque<String> history = new ArrayDeque<>();
    private transient Migrations.MigrationResult lastAppliedMigrationResult;

    public InMemoryConfigStore(Gson gson) {
        this(gson, 50);
    }

    public InMemoryConfigStore(Gson gson, int maxHistorySize) {
        this.gson = gson;
        this.maxHistorySize = maxHistorySize;
    }

    @Override
    public boolean exists() {
        return data != null;
    }

    @Override
    public CommonBaseConfig read(Class<? extends CommonBaseConfig> clazz,
                                 Migrations migrations,
                                 CommonBaseConfig defaults) {
        JsonObject jsonObject = JsonParser.parseString(data)
                                          .getAsJsonObject();
        int storedVersion = jsonObject.has(ConfigKeys.VERSION) ? jsonObject.get(ConfigKeys.VERSION)
                                                                           .getAsInt() : 0;
        Migrations.MigrationResult migrationResult;
        try {
            migrationResult = migrations.execute(storedVersion, gson, jsonObject);
        } catch (MigrationExecutionException e) {
            logger.severe(e.getMessage());
            throw e;
        }
        lastAppliedMigrationResult = migrationResult.migrated() ? migrationResult : null;
        if (migrationResult.migrated()) {
            jsonObject = migrationResult.document();
            jsonObject.addProperty(ConfigKeys.VERSION, migrations.latestVersion());
            data = gson.toJson(jsonObject);
            logMigrationResult(migrationResult);
        }
        JsonObject defaultObject = JsonConfigDefaults.fromConfig(defaults, gson);
        JsonObject merged = JsonConfigDefaults.fillMissing(jsonObject, defaultObject);
        return gson.fromJson(merged, clazz);
    }

    @Override
    public CommonBaseConfig write(CommonBaseConfig config,
                                  Class<? extends CommonBaseConfig> clazz,
                                  Migrations migrations) {
        data = gson.toJson(config);
        return gson.fromJson(data, clazz);
    }

    @Override
    public Object copyValue(Type type, Object value) {
        if (value == null) {
            return null;
        }
        return gson.fromJson(gson.toJsonTree(value, type), type);
    }

    @Override
    public Optional<Migrations.MigrationResult> previewMigrations(Migrations migrations) {
        if (!exists()) {
            return Optional.empty();
        }

        JsonObject jsonObject = JsonParser.parseString(data)
                                          .getAsJsonObject();
        int storedVersion = jsonObject.has(ConfigKeys.VERSION) ? jsonObject.get(ConfigKeys.VERSION)
                                                                           .getAsInt() : 0;
        return Optional.of(migrations.execute(storedVersion, gson, jsonObject));
    }

    public void write(CommonBaseConfig config) {
        data = gson.toJson(config);
    }

    @Override
    public void pushHistory(CommonBaseConfig config, HistorySource source) {
        JsonObject obj = JsonParser.parseString(gson.toJson(config))
                                   .getAsJsonObject();
        obj.addProperty(ConfigKeys.TIMESTAMP, System.currentTimeMillis());
        obj.addProperty(ConfigKeys.HISTORY_SOURCE, source.name());
        history.addFirst(gson.toJson(obj));
        while (history.size() > maxHistorySize) {
            history.removeLast();
        }
    }

    public void pushHistory(CommonBaseConfig config) {
        pushHistory(config, HistorySource.PROGRAMMATIC);
    }

    @Override
    public boolean canUndo(int stepsBack) {
        return history.size() >= stepsBack + 1;
    }

    @Override
    public CommonBaseConfig undo(Class<? extends CommonBaseConfig> clazz, Migrations migrations, int stepsBack) {
        for (int i = 0; i < stepsBack; i++) {
            history.removeFirst();
        }
        String snapshot = history.peekFirst();
        return gson.fromJson(JsonParser.parseString(snapshot)
                                       .getAsJsonObject(), clazz);
    }

    @Override
    public List<HistoryEntry> readHistory(Class<? extends CommonBaseConfig> clazz, Migrations migrations) {
        List<HistoryEntry> result = new ArrayList<>();
        for (String snapshot : history) {
            JsonObject obj = JsonParser.parseString(snapshot)
                                       .getAsJsonObject();
            long ts = obj.has(ConfigKeys.TIMESTAMP) ? obj.get(ConfigKeys.TIMESTAMP)
                                                         .getAsLong() : 0L;
            HistorySource source = obj.has(ConfigKeys.HISTORY_SOURCE) ? HistorySource.valueOf(obj.get(ConfigKeys.HISTORY_SOURCE)
                                                                                                 .getAsString()) : HistorySource.PROGRAMMATIC;
            result.add(new HistoryEntry(ts, source, gson.fromJson(obj, clazz)));
        }
        return result;
    }

    @Override
    public Optional<Migrations.MigrationResult> lastAppliedMigrationResult() {
        return Optional.ofNullable(lastAppliedMigrationResult);
    }

    /**
     * For testing: directly set the raw JSON string as stored content.
     */
    public void writeRaw(String json) {
        data = json;
    }

    /**
     * For testing: get the raw JSON string as currently stored.
     */
    public String readRaw() {
        return data;
    }

    @Override
    public Closeable startWatching(Timer timer, Runnable onChanged, int periodMs) {
        return () -> {
        };
    }

    private void logMigrationResult(Migrations.MigrationResult migrationResult) {
        logger.info("Applied config migrations from v" + migrationResult.storedVersion() + " to v" + migrationResult.targetVersion());
        for (Migrations.VersionReport versionReport : migrationResult.versionReports()) {
            for (Migrations.OperationReport operation : versionReport.operations()) {
                if (!operation.applied()) {
                    continue;
                }
                logger.info("  v" + versionReport.version() + ": " + formatOperation(operation));
            }
        }
    }

    private static String formatOperation(Migrations.OperationReport operation) {
        if (operation.targetPath() != null) {
            return operation.type()
                            .displayName() + " " + operation.path() + " -> " + operation.targetPath();
        }
        return operation.type()
                        .displayName() + " " + operation.path();
    }
}
