package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.JsonMigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.util.*;

public class InMemoryConfigStore implements ConfigStore {
    private final Gson gson;
    private final int maxHistorySize;
    private String data = null;
    private final Deque<String> history = new ArrayDeque<>();

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
    public CommonBaseConfig read(Class<? extends CommonBaseConfig> clazz, Migrations migrations) {
        JsonObject jsonObject = JsonParser.parseString(data)
                                          .getAsJsonObject();
        int storedVersion = jsonObject.has("_version_") ? jsonObject.get("_version_")
                                                                    .getAsInt() : 0;
        if (migrations.apply(storedVersion, new JsonMigrationContext(gson, jsonObject))) {
            jsonObject.addProperty("_version_", migrations.latestVersion());
            data = gson.toJson(jsonObject);
        }
        return gson.fromJson(jsonObject, clazz);
    }

    @Override
    public void write(CommonBaseConfig config) {
        data = gson.toJson(config);
    }

    @Override
    public void pushHistory(CommonBaseConfig config) {
        JsonObject obj = JsonParser.parseString(gson.toJson(config))
                                   .getAsJsonObject();
        obj.addProperty("_ts_", System.currentTimeMillis());
        history.addFirst(gson.toJson(obj));
        while (history.size() > maxHistorySize) {
            history.removeLast();
        }
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
            long ts = obj.has("_ts_") ? obj.get("_ts_")
                                           .getAsLong() : 0L;
            result.add(new HistoryEntry(ts, gson.fromJson(obj, clazz)));
        }
        return result;
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
}
