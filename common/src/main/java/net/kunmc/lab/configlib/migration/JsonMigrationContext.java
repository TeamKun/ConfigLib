package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

final class JsonMigrationContext {
    private final Gson gson;
    private final JsonObject root;

    JsonMigrationContext(Gson gson, JsonObject root) {
        this.gson = gson;
        this.root = root;
    }

    boolean has(String path) {
        return resolve(path).exists();
    }

    <T> T get(String path, Class<T> type) {
        PathLocation location = resolve(path);
        if (!location.exists()) {
            return null;
        }
        return gson.fromJson(location.value(), type);
    }

    boolean set(String path, Object value) {
        setElement(path, gson.toJsonTree(value));
        return true;
    }

    boolean defaultValue(String path, Object value) {
        if (!has(path)) {
            set(path, value);
            return true;
        }
        return false;
    }

    boolean delete(String path) {
        PathLocation location = resolve(path);
        if (!location.exists()) {
            return false;
        }
        location.parent()
                .remove(location.leaf());
        return true;
    }

    boolean rename(String from, String to) {
        return move(from, to);
    }

    boolean move(String from, String to) {
        if (Objects.equals(from, to)) {
            return false;
        }

        PathLocation source = resolve(from);
        if (!source.exists()) {
            return false;
        }

        JsonElement value = source.value()
                                  .deepCopy();
        source.parent()
              .remove(source.leaf());
        setElement(to, value);
        return true;
    }

    <S, T> boolean convert(String path,
                           Class<S> sourceType,
                           Class<T> targetType,
                           Function<? super S, ? extends T> converter) {
        PathLocation location = resolve(path);
        if (!location.exists()) {
            return false;
        }

        S source = gson.fromJson(location.value(), sourceType);
        T target = converter.apply(source);
        JsonElement converted = target == null ? JsonNull.INSTANCE : gson.toJsonTree(target, targetType);
        setElement(path, converted);
        return true;
    }

    private void setElement(String path, JsonElement value) {
        String[] segments = segments(path);
        JsonObject current = root;
        for (int i = 0; i < segments.length - 1; i++) {
            String segment = segments[i];
            JsonElement child = current.get(segment);
            if (child == null || child.isJsonNull()) {
                JsonObject next = new JsonObject();
                current.add(segment, next);
                current = next;
                continue;
            }
            if (!child.isJsonObject()) {
                throw new IllegalArgumentException("Cannot create nested migration path through non-object node: " + path);
            }
            current = child.getAsJsonObject();
        }
        current.add(segments[segments.length - 1], value == null ? JsonNull.INSTANCE : value);
    }

    private PathLocation resolve(String path) {
        String[] segments = segments(path);
        JsonObject current = root;
        for (int i = 0; i < segments.length - 1; i++) {
            JsonElement child = current.get(segments[i]);
            if (child == null || child.isJsonNull() || !child.isJsonObject()) {
                return PathLocation.missing(current, segments[segments.length - 1]);
            }
            current = child.getAsJsonObject();
        }

        String leaf = segments[segments.length - 1];
        if (!current.has(leaf)) {
            return PathLocation.missing(current, leaf);
        }
        return PathLocation.present(current, leaf, current.get(leaf));
    }

    private static String[] segments(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Migration path must not be blank");
        }

        String[] segments = Arrays.stream(path.split("\\."))
                                  .map(String::trim)
                                  .toArray(String[]::new);
        if (segments.length == 0 || Arrays.stream(segments)
                                          .anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Invalid migration path: " + path);
        }
        return segments;
    }

    private static final class PathLocation {
        private final JsonObject parent;
        private final String leaf;
        private final JsonElement value;

        private PathLocation(JsonObject parent, String leaf, JsonElement value) {
            this.parent = parent;
            this.leaf = leaf;
            this.value = value;
        }

        static PathLocation present(JsonObject parent, String leaf, JsonElement value) {
            return new PathLocation(parent, leaf, value);
        }

        static PathLocation missing(JsonObject parent, String leaf) {
            return new PathLocation(parent, leaf, null);
        }

        boolean exists() {
            return value != null;
        }

        JsonObject parent() {
            return parent;
        }

        String leaf() {
            return leaf;
        }

        JsonElement value() {
            return value;
        }
    }
}
