package net.kunmc.lab.configlib.store;

import com.google.gson.*;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

import java.math.BigDecimal;
import java.util.*;

public final class YamlConfigFormat implements ConfigFormat {
    private final Gson gson;
    private final Load load;
    private final Dump dump;

    public YamlConfigFormat(Gson gson) {
        this.gson = gson;
        LoadSettings loadSettings = LoadSettings.builder()
                                                .setDefaultMap(LinkedHashMap::new)
                                                .setParseComments(true)
                                                .build();
        DumpSettings dumpSettings = DumpSettings.builder()
                                                .setDefaultFlowStyle(FlowStyle.BLOCK)
                                                .setIndent(2)
                                                .build();
        this.load = new Load(loadSettings);
        this.dump = new Dump(dumpSettings);
    }

    @Override
    public String extension() {
        return "yml";
    }

    @Override
    public JsonElement parse(String content) {
        Object loaded = load.loadFromString(content);
        if (loaded == null) {
            return new JsonObject();
        }
        return toJsonElement(loaded);
    }

    @Override
    public boolean wrapsHistory() {
        return true;
    }

    @Override
    public String write(JsonElement element) {
        Object value = toYamlValue(element);
        String yaml = dump.dumpToString(value);
        if (element.isJsonObject()) {
            yaml = addTopLevelDescriptionComments(yaml, element.getAsJsonObject());
        }
        return yaml;
    }

    private JsonElement toJsonElement(Object value) {
        if (value == null) {
            return JsonNull.INSTANCE;
        }
        if (value instanceof Map) {
            JsonObject object = new JsonObject();
            ((Map<?, ?>) value).forEach((k, v) -> object.add(String.valueOf(k), toJsonElement(v)));
            return object;
        }
        if (value instanceof Iterable) {
            JsonArray array = new JsonArray();
            ((Iterable<?>) value).forEach(v -> array.add(toJsonElement(v)));
            return array;
        }
        if (value instanceof String) {
            return new JsonPrimitive((String) value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        }
        return gson.toJsonTree(value);
    }

    private Object toYamlValue(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            element.getAsJsonObject()
                   .entrySet()
                   .stream()
                   .filter(e -> !"description".equals(e.getKey()))
                   .forEach(e -> map.put(e.getKey(), toYamlValue(e.getValue())));
            return map;
        }
        if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            element.getAsJsonArray()
                   .forEach(e -> list.add(toYamlValue(e)));
            return list;
        }
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }
        if (primitive.isNumber()) {
            return normalizeNumber(primitive);
        }
        return primitive.getAsString();
    }

    private Number normalizeNumber(JsonPrimitive primitive) {
        BigDecimal decimal = primitive.getAsBigDecimal();
        try {
            return decimal.intValueExact();
        } catch (ArithmeticException ignored) {
        }
        try {
            return decimal.longValueExact();
        } catch (ArithmeticException ignored) {
        }
        return decimal.doubleValue();
    }

    private String addTopLevelDescriptionComments(String yaml, JsonObject root) {
        Map<String, String> comments = new HashMap<>();
        root.entrySet()
            .stream()
            .filter(e -> e.getValue()
                          .isJsonObject())
            .forEach(e -> {
                JsonObject object = e.getValue()
                                     .getAsJsonObject();
                if (object.has("description") && !object.get("description")
                                                        .isJsonNull()) {
                    comments.put(e.getKey(),
                                 object.get("description")
                                       .getAsString());
                }
            });

        if (comments.isEmpty()) {
            return yaml;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = yaml.split("\\R", -1);
        for (String line : lines) {
            String key = topLevelKey(line);
            if (key != null && comments.containsKey(key)) {
                appendComment(result, comments.get(key));
            }
            result.append(line)
                  .append(System.lineSeparator());
        }
        return result.toString();
    }

    private String topLevelKey(String line) {
        if (line.isEmpty() || Character.isWhitespace(line.charAt(0)) || line.startsWith("-")) {
            return null;
        }
        int index = line.indexOf(':');
        if (index <= 0) {
            return null;
        }
        return line.substring(0, index);
    }

    private void appendComment(StringBuilder result, String comment) {
        Arrays.stream(comment.split("\\R", -1))
              .forEach(line -> result.append("# ")
                                     .append(line)
                                     .append(System.lineSeparator()));
    }
}
