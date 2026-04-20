package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;

import java.lang.reflect.Field;

final class JsonConfigDefaults {
    private JsonConfigDefaults() {
    }

    static JsonObject fromConfig(CommonBaseConfig config, Gson gson) {
        JsonObject result = new JsonObject();
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())) {
            if (!ConfigUtil.isObservableField(config, field)) {
                continue;
            }
            if (ConfigStore.class.isAssignableFrom(field.getType())) {
                continue;
            }
            if (!field.trySetAccessible()) {
                continue;
            }

            try {
                result.add(field.getName(), gson.toJsonTree(field.get(config)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    static JsonObject fillMissing(JsonObject target, JsonObject defaults) {
        JsonObject result = target.deepCopy();
        fillMissingInPlace(result, defaults);
        return result;
    }

    private static void fillMissingInPlace(JsonObject target, JsonObject defaults) {
        for (String key : defaults.keySet()) {
            JsonElement defaultValue = defaults.get(key);
            if (!target.has(key)) {
                target.add(key, defaultValue.deepCopy());
                continue;
            }

            JsonElement currentValue = target.get(key);
            if (currentValue != null && currentValue.isJsonObject() && defaultValue.isJsonObject()) {
                fillMissingInPlace(currentValue.getAsJsonObject(), defaultValue.getAsJsonObject());
            }
        }
    }
}
