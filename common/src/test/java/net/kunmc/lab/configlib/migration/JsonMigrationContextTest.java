package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonMigrationContextTest {
    private final Gson gson = new Gson();

    private JsonObject json(String raw) {
        return JsonParser.parseString(raw)
                         .getAsJsonObject();
    }

    private JsonMigrationContext ctxOn(JsonObject json) {
        return new JsonMigrationContext(gson, json);
    }

    @Test
    void renameSupportsNestedPaths() {
        JsonObject json = json("{\"database\":{\"host\":\"localhost\"}}");
        ctxOn(json).rename("database.host", "storage.mysql.host");

        assertFalse(json.getAsJsonObject("database")
                        .has("host"));
        assertEquals("localhost",
                     json.getAsJsonObject("storage")
                         .getAsJsonObject("mysql")
                         .get("host")
                         .getAsString());
    }

    @Test
    void moveIgnoresMissingSourcePath() {
        JsonObject json = json("{}");

        assertDoesNotThrow(() -> ctxOn(json).move("missing.path", "new.path"));
        assertFalse(json.has("new"));
    }

    @Test
    void deleteSupportsNestedPaths() {
        JsonObject json = json("{\"outer\":{\"inner\":1,\"keep\":2}}");
        ctxOn(json).delete("outer.inner");

        assertFalse(json.getAsJsonObject("outer")
                        .has("inner"));
        assertEquals(2,
                     json.getAsJsonObject("outer")
                         .get("keep")
                         .getAsInt());
    }

    @Test
    void setCreatesIntermediateObjects() {
        JsonObject json = json("{}");
        ctxOn(json).set("outer.inner.value", 42);

        assertEquals(42,
                     json.getAsJsonObject("outer")
                         .getAsJsonObject("inner")
                         .get("value")
                         .getAsInt());
    }

    @Test
    void defaultValueOnlyWritesWhenPathMissing() {
        JsonObject json = json("{\"present\":5}");
        JsonMigrationContext context = ctxOn(json);

        context.defaultValue("present", 10);
        context.defaultValue("missing", 20);

        assertEquals(5,
                     json.get("present")
                         .getAsInt());
        assertEquals(20,
                     json.get("missing")
                         .getAsInt());
    }

    @Test
    void convertRewritesExistingValue() {
        JsonObject json = json("{\"timeout\":\"15\"}");
        ctxOn(json).convert("timeout", String.class, Integer.class, Integer::parseInt);

        assertEquals(15,
                     json.get("timeout")
                         .getAsInt());
    }

    @Test
    void convertIgnoresMissingPath() {
        JsonObject json = json("{}");

        assertDoesNotThrow(() -> ctxOn(json).convert("timeout", String.class, Integer.class, Integer::parseInt));
        assertFalse(json.has("timeout"));
    }

    @Test
    void hasSupportsNestedPaths() {
        JsonMigrationContext context = ctxOn(json("{\"outer\":{\"inner\":1}}"));

        assertTrue(context.has("outer.inner"));
        assertFalse(context.has("outer.missing"));
    }
}
