package net.kunmc.lab.configlib.store;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface ConfigFormat {
    String extension();

    JsonElement parse(String content);

    default JsonObject parseObject(String content) {
        return parse(content).getAsJsonObject();
    }

    default boolean wrapsHistory() {
        return false;
    }

    String write(JsonElement element);
}
