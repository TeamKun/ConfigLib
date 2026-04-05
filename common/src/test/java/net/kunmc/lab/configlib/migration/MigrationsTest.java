package net.kunmc.lab.configlib.migration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class MigrationsTest {
    private final Gson gson = new Gson();

    private Migrations manager(TreeMap<Integer, Consumer<MigrationContext>> migrations) {
        return new Migrations(migrations);
    }

    private JsonObject json(String raw) {
        return JsonParser.parseString(raw)
                         .getAsJsonObject();
    }

    private boolean apply(Migrations mgr, JsonObject j) {
        int storedVersion = j.has("_version_") ? j.get("_version_")
                                                  .getAsInt() : 0;
        return mgr.apply(storedVersion, new JsonMigrationContext(gson, j));
    }

    // ---- latestVersion ----

    @Test
    void latestVersionReturnsZeroWhenEmpty() {
        assertEquals(0, manager(new TreeMap<>()).latestVersion());
    }

    @Test
    void latestVersionReturnsHighestKey() {
        TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();
        migrations.put(1, ctx -> {
        });
        migrations.put(3, ctx -> {
        });
        migrations.put(2, ctx -> {
        });

        assertEquals(3, manager(migrations).latestVersion());
    }

    // ---- スキップ ----

    @Test
    void returnsFalseWhenNoMigrations() {
        assertFalse(apply(manager(new TreeMap<>()), json("{\"value\": 42}")));
    }

    @Test
    void returnsFalseWhenAlreadyLatestVersion() {
        TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();
        migrations.put(1, ctx -> ctx.setString("field", "should_not_run"));

        JsonObject j = json("{\"_version_\": 1, \"field\": \"original\"}");
        assertFalse(apply(manager(migrations), j));
        assertEquals("original",
                     j.get("field")
                      .getAsString());
    }

    // ---- バージョン適用 ----

    @Test
    void appliesAllMigrationsFromVersionZero() {
        TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();
        migrations.put(1, ctx -> ctx.setInt("value", ctx.getInt("value") + 1));
        migrations.put(2, ctx -> ctx.setInt("value", ctx.getInt("value") * 10));

        JsonObject j = json("{\"value\": 1}");
        assertTrue(apply(manager(migrations), j));

        // (1+1)*10 = 20
        assertEquals(20,
                     j.get("value")
                      .getAsInt());
    }

    @Test
    void appliesOnlyRemainingMigrationsFromPartialVersion() {
        TreeMap<Integer, Consumer<MigrationContext>> migrations = new TreeMap<>();
        migrations.put(1, ctx -> ctx.setString("field", "v1"));
        migrations.put(2, ctx -> ctx.setString("field", "v2"));
        migrations.put(3, ctx -> ctx.setString("field", "v3"));

        JsonObject j = json("{\"_version_\": 1, \"field\": \"original\"}");
        assertTrue(apply(manager(migrations), j));
        assertEquals("v3",
                     j.get("field")
                      .getAsString());
    }
}
