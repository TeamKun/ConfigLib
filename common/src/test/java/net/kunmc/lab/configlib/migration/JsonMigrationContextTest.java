package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonMigrationContextTest {
    private final Gson gson = new Gson();

    private MigrationContext ctx(String raw) {
        return new JsonMigrationContext(gson,
                                        JsonParser.parseString(raw)
                                                  .getAsJsonObject());
    }

    private JsonObject json(String raw) {
        return JsonParser.parseString(raw)
                         .getAsJsonObject();
    }

    private JsonMigrationContext ctxOn(JsonObject j) {
        return new JsonMigrationContext(gson, j);
    }

    @Test
    void rename() {
        JsonObject j = json("{\"oldName\": \"value\"}");
        ctxOn(j).rename("oldName", "newName");

        assertFalse(j.has("oldName"));
        assertEquals("value",
                     j.get("newName")
                      .getAsString());
    }

    @Test
    void renameIgnoresNonExistentKey() {
        JsonObject j = json("{}");
        assertDoesNotThrow(() -> ctxOn(j).rename("missing", "newName"));
        assertFalse(j.has("newName"));
    }

    @Test
    void remove() {
        JsonObject j = json("{\"field\": \"value\"}");
        ctxOn(j).remove("field");

        assertFalse(j.has("field"));
    }

    @Test
    void has() {
        MigrationContext present = ctx("{\"key\": 1}");
        MigrationContext absent = ctx("{}");

        assertTrue(present.has("key"));
        assertFalse(absent.has("key"));
    }

    @Test
    void setAndGetString() {
        JsonObject j = json("{\"key\": \"hello\"}");
        JsonMigrationContext c = ctxOn(j);
        c.setString("key", c.getString("key") + "_migrated");

        assertEquals("hello_migrated",
                     j.get("key")
                      .getAsString());
    }

    @Test
    void setAndGetInt() {
        JsonObject j = json("{\"key\": 5}");
        JsonMigrationContext c = ctxOn(j);
        c.setInt("key", c.getInt("key") * 1000);

        assertEquals(5000,
                     j.get("key")
                      .getAsInt());
    }

    @Test
    void setAndGetDouble() {
        JsonObject j = json("{\"key\": 1.5}");
        JsonMigrationContext c = ctxOn(j);
        c.setDouble("key", c.getDouble("key") * 2.0);

        assertEquals(3.0,
                     j.get("key")
                      .getAsDouble(),
                     1e-9);
    }

    @Test
    void setAndGetBoolean() {
        JsonObject j = json("{\"key\": true}");
        JsonMigrationContext c = ctxOn(j);
        c.setBoolean("key", !c.getBoolean("key"));

        assertFalse(j.get("key")
                     .getAsBoolean());
    }

    // ---- getObject / setObject ----

    @Test
    void setAndGetObjectByClass() {
        JsonObject j = json("{\"point\": {\"x\": 1, \"y\": 2}}");
        JsonMigrationContext c = ctxOn(j);

        Point p = c.getObject("point", Point.class);
        assertEquals(1, p.x);
        assertEquals(2, p.y);

        c.setObject("point", new Point(10, 20));
        Point updated = c.getObject("point", Point.class);
        assertEquals(10, updated.x);
        assertEquals(20, updated.y);
    }

    @Test
    void setAndGetGenericObjectByType() {
        JsonObject j = json("{\"tags\": [\"a\", \"b\", \"c\"]}");
        JsonMigrationContext c = ctxOn(j);

        List<String> tags = c.getObject("tags", new TypeToken<List<String>>() {
        }.getType());
        assertEquals(Arrays.asList("a", "b", "c"), tags);

        c.setObject("tags", Arrays.asList("x", "y"));
        List<String> updated = c.getObject("tags", new TypeToken<List<String>>() {
        }.getType());
        assertEquals(Arrays.asList("x", "y"), updated);
    }

    static class Point {
        int x, y;

        Point() {
        }

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
