package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryConfigStoreTest {
    private final Gson gson = new Gson();

    private InMemoryConfigStore storeWith(String json) {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.writeRaw(json);
        return store;
    }

    private Migrations noMigrations() {
        return new Migrations(new TreeMap<>());
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
        store.read(SimpleConfig.class, migrations(m));

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
        store.read(SimpleConfig.class, migrations(m));

        assertEquals(original, store.readRaw());
    }

    // ---- history: pushHistory / readHistory ----

    @Test
    void pushHistoryStoresEntry() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(5));

        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void pushHistoryEmbeddsTimestamp() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        long before = System.currentTimeMillis();
        store.pushHistory(new ValueConfig(0));
        long after = System.currentTimeMillis();

        HistoryEntry entry = store.readHistory(ValueConfig.class, noMigrations())
                                  .get(0);
        assertTrue(entry.timestamp() >= before && entry.timestamp() <= after);
    }

    @Test
    void readHistoryReturnsNewestFirst() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(1));
        store.pushHistory(new ValueConfig(2));

        List<HistoryEntry> history = store.readHistory(ValueConfig.class, noMigrations());
        assertEquals(2,
                     ((ValueConfig) history.get(0)
                                           .config()).value);
        assertEquals(1,
                     ((ValueConfig) history.get(1)
                                           .config()).value);
    }

    @Test
    void historyCapAtMaxSize() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson, 3);
        for (int i = 0; i < 5; i++) {
            store.pushHistory(new ValueConfig(i));
        }

        assertEquals(3,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    // ---- history: canUndo ----

    @Test
    void canUndoReturnsFalseWhenHistoryEmpty() {
        assertFalse(new InMemoryConfigStore(gson).canUndo(1));
    }

    @Test
    void canUndoReturnsFalseWithSingleEntry() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0));
        assertFalse(store.canUndo(1));
    }

    @Test
    void canUndoReturnsTrueWithTwoEntries() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0));
        store.pushHistory(new ValueConfig(1));
        assertTrue(store.canUndo(1));
    }

    @Test
    void canUndoRespectsStepsBack() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0));
        store.pushHistory(new ValueConfig(1));
        // stepsBack=2 には3件必要
        assertFalse(store.canUndo(2));
        store.pushHistory(new ValueConfig(2));
        assertTrue(store.canUndo(2));
    }

    // ---- history: undo ----

    @Test
    void undoRemovesTopAndReturnsNewTop() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(10)); // old
        store.pushHistory(new ValueConfig(20)); // current

        ValueConfig restored = (ValueConfig) store.undo(ValueConfig.class, noMigrations(), 1);

        assertEquals(10, restored.value);
        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void undoWithStepsBack2() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0));
        store.pushHistory(new ValueConfig(10));
        store.pushHistory(new ValueConfig(20));
        // history: [20, 10, 0]

        ValueConfig restored = (ValueConfig) store.undo(ValueConfig.class, noMigrations(), 2);

        assertEquals(0, restored.value);
        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    // ---- inner classes ----

    static class SimpleConfig extends CommonBaseConfig {
        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class ValueConfig extends CommonBaseConfig {
        int value;

        ValueConfig() {
        }

        ValueConfig(int value) {
            this.value = value;
        }

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }
}
