package net.kunmc.lab.configlib.migration;

import java.lang.reflect.Type;

public interface MigrationContext {
    boolean has(String key);

    String getString(String key);

    int getInt(String key);

    double getDouble(String key);

    boolean getBoolean(String key);

    <T> T getObject(String key, Class<T> clazz);

    <T> T getObject(String key, Type type);

    void setString(String key, String value);

    void setInt(String key, int value);

    void setDouble(String key, double value);

    void setBoolean(String key, boolean value);

    void setObject(String key, Object value);

    void rename(String from, String to);

    void remove(String key);
}
