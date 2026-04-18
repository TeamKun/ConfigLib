package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class JsonConfigFormat implements ConfigFormat {
    private final Gson gson;

    public JsonConfigFormat(Gson gson) {
        this.gson = gson;
    }

    @Override
    public String extension() {
        return "json";
    }

    @Override
    public JsonElement parse(String content) {
        return JsonParser.parseString(content);
    }

    @Override
    public String write(JsonElement element) {
        return gson.toJson(element);
    }
}
