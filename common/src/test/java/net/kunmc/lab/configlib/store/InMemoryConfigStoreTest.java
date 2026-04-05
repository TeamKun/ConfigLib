package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryConfigStoreTest {
    private final Gson gson = new Gson();

    private InMemoryConfigStore storeWith(String json) {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.writeRaw(json);
        return store;
    }

    private Migrations migrations(TreeMap<Integer, Consumer<MigrationContext>> m) {
        return new Migrations(m);
    }

    // ---- マイグレーション後の保存 ----

    @Test
    void updatesStoreAfterMigration() {
        TreeMap<Integer, Consumer<MigrationContext>> m = new TreeMap<>();
        m.put(1, ctx -> ctx.rename("old", "new"));

        InMemoryConfigStore store = storeWith("{\"old\": \"value\"}");
        store.read(TestConfig.class, migrations(m));

        JsonObject saved = JsonParser.parseString(store.readRaw())
                                     .getAsJsonObject();
        assertFalse(saved.has("old"));
        assertEquals("value",
                     saved.get("new")
                          .getAsString());
        assertEquals(1,
                     saved.get("_version_")
                          .getAsInt());
    }

    @Test
    void doesNotUpdateStoreWhenNoMigrationNeeded() {
        TreeMap<Integer, Consumer<MigrationContext>> m = new TreeMap<>();
        m.put(1, ctx -> ctx.setString("field", "migrated"));

        String original = "{\"_version_\": 1, \"field\": \"original\"}";
        InMemoryConfigStore store = storeWith(original);
        store.read(TestConfig.class, migrations(m));

        assertEquals(original, store.readRaw());
    }

    // テスト用の最小 CommonBaseConfig 実装
    private static class TestConfig extends net.kunmc.lab.configlib.CommonBaseConfig {
        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }
}
