package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class JsonMigrationContext implements MigrationContext {
    private final Gson gson;
    private final JsonObject jsonObject;

    public JsonMigrationContext(Gson gson, JsonObject jsonObject) {
        this.gson = gson;
        this.jsonObject = jsonObject;
    }

    @Override
    public boolean has(String key) {
        return jsonObject.has(key);
    }

    @Override
    public String getString(String key) {
        return jsonObject.get(key)
                         .getAsString();
    }

    @Override
    public int getInt(String key) {
        return jsonObject.get(key)
                         .getAsInt();
    }

    @Override
    public double getDouble(String key) {
        return jsonObject.get(key)
                         .getAsDouble();
    }

    @Override
    public boolean getBoolean(String key) {
        return jsonObject.get(key)
                         .getAsBoolean();
    }

    @Override
    public <T> T getObject(String key, Class<T> clazz) {
        return gson.fromJson(jsonObject.get(key), clazz);
    }

    @Override
    public <T> T getObject(String key, Type type) {
        return gson.fromJson(jsonObject.get(key), type);
    }

    @Override
    public void setString(String key, String value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void setInt(String key, int value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void setDouble(String key, double value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void setObject(String key, Object value) {
        jsonObject.add(key, gson.toJsonTree(value));
    }

    @Override
    public void rename(String from, String to) {
        if (jsonObject.has(from)) {
            jsonObject.add(to, jsonObject.remove(from));
        }
    }

    @Override
    public void remove(String key) {
        jsonObject.remove(key);
    }
}
