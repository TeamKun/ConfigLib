package net.kunmc.lab.configlib.store;

import com.google.gson.*;
import net.kunmc.lab.configlib.ConfigKeys;
import net.kunmc.lab.configlib.schema.ConfigSchema;
import org.jetbrains.annotations.Nullable;
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
    public String write(JsonElement element, @Nullable ConfigSchema schema) {
        Object value = toYamlValue(element);
        String yaml = dump.dumpToString(value);
        if (element.isJsonObject()) {
            yaml = addDescriptionComments(yaml, element.getAsJsonObject(), schema);
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
                   .filter(e -> !ConfigKeys.DESCRIPTION.equals(e.getKey()))
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

    private String addDescriptionComments(String yaml, JsonObject root, @Nullable ConfigSchema schema) {
        Map<String, String> comments = new LinkedHashMap<>();
        if (schema != null) {
            comments.putAll(schema.descriptionsByPath());
        }
        root.entrySet()
            .stream()
            .filter(e -> e.getValue()
                          .isJsonObject())
            .forEach(e -> {
                JsonObject object = e.getValue()
                                     .getAsJsonObject();
                if (object.has(ConfigKeys.DESCRIPTION) && !object.get(ConfigKeys.DESCRIPTION)
                                                                 .isJsonNull()) {
                    comments.putIfAbsent(e.getKey(),
                                         object.get(ConfigKeys.DESCRIPTION)
                                               .getAsString());
                }
            });

        if (comments.isEmpty()) {
            return yaml;
        }

        StringBuilder result = new StringBuilder();
        String[] lines = yaml.split("\\R", -1);
        List<PathSegment> path = new ArrayList<>();
        for (String line : lines) {
            String key = keyOf(line);
            if (key != null) {
                int indent = indentationOf(line);
                path.removeIf(segment -> segment.indent >= indent);
                path.add(new PathSegment(indent, key));

                String currentPath = pathString(path);
                if (comments.containsKey(currentPath)) {
                    appendComment(result, indent, comments.get(currentPath));
                }
            }
            result.append(line)
                  .append(System.lineSeparator());
        }
        return result.toString();
    }

    private int indentationOf(String line) {
        int indent = 0;
        while (indent < line.length() && Character.isWhitespace(line.charAt(indent))) {
            indent++;
        }
        return indent;
    }

    private String keyOf(String line) {
        if (line.isEmpty()) {
            return null;
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("-") || trimmed.startsWith("#")) {
            return null;
        }
        int index = trimmed.indexOf(':');
        if (index <= 0) {
            return null;
        }
        return unquote(trimmed.substring(0, index));
    }

    private String unquote(String key) {
        if (key.length() >= 2 && ((key.startsWith("\"") && key.endsWith("\"")) || (key.startsWith("'") && key.endsWith(
                "'")))) {
            return key.substring(1, key.length() - 1);
        }
        return key;
    }

    private String pathString(List<PathSegment> path) {
        StringBuilder sb = new StringBuilder();
        for (PathSegment segment : path) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(segment.key);
        }
        return sb.toString();
    }

    private void appendComment(StringBuilder result, int indent, String comment) {
        String prefix = " ".repeat(indent);
        Arrays.stream(comment.split("\\R", -1))
              .forEach(line -> result.append(prefix)
                                     .append("# ")
                                     .append(line)
                                     .append(System.lineSeparator()));
    }

    private static final class PathSegment {
        private final int indent;
        private final String key;

        private PathSegment(int indent, String key) {
            this.indent = indent;
            this.key = key;
        }
    }
}
