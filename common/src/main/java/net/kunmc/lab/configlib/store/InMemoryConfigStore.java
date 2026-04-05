package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.JsonMigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;

import java.io.Closeable;
import java.util.Timer;

public class InMemoryConfigStore implements ConfigStore {
    private final Gson gson;
    private String data = null;

    public InMemoryConfigStore(Gson gson) {
        this.gson = gson;
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
    public Closeable startWatching(Timer timer, Runnable onChanged) {
        return () -> {
        };
    }
}
