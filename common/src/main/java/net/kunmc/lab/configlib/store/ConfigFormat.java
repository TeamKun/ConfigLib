package net.kunmc.lab.configlib.store;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kunmc.lab.configlib.schema.ConfigSchema;
import org.jetbrains.annotations.Nullable;

public interface ConfigFormat {
    String extension();

    JsonElement parse(String content);

    default JsonObject parseObject(String content) {
        return parse(content).getAsJsonObject();
    }

    default boolean wrapsHistory() {
        return false;
    }

    String write(JsonElement element, @Nullable ConfigSchema schema);
}
