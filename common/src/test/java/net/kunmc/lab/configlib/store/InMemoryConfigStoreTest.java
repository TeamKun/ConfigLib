package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.MigrationExecutionException;
import net.kunmc.lab.configlib.migration.MigrationOperationType;
import net.kunmc.lab.configlib.migration.Migrations;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryConfigStoreTest {
    private final Gson gson = new Gson();

    private InMemoryConfigStore storeWith(String json) {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.writeRaw(json);
        return store;
    }

    private Migrations noMigrations() {
        return Migrations.empty();
    }

    // ---- マイグレーション後の保存 ----

    @Test
    void updatesStoreAfterMigration() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.rename("old", "new"))
                                          .build();

        InMemoryConfigStore store = storeWith("{\"old\": \"value\"}");
        store.read(SimpleConfig.class, migrations, new SimpleConfig());

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
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.set("field", "migrated"))
                                          .build();

        String original = "{\"_version_\": 1, \"field\": \"original\"}";
        InMemoryConfigStore store = storeWith(original);
        store.read(SimpleConfig.class, migrations, new SimpleConfig());

        assertEquals(original, store.readRaw());
    }

    @Test
    void logsAppliedMigrationSummary() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.rename("old", "new"))
                                          .build();
        InMemoryConfigStore store = storeWith("{\"old\":\"value\"}");
        Logger logger = Logger.getLogger(InMemoryConfigStore.class.getName());
        TestLogHandler handler = new TestLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        try {
            store.read(SimpleConfig.class, migrations, new SimpleConfig());
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(true);
        }

        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains("Applied config migrations from v0 to v1")));
        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains("v1: rename old -> new")));
    }

    @Test
    void logsMigrationFailureDetails() {
        Migrations migrations = Migrations.builder()
                                          .migrateTo(1, migration -> migration.set("broken.child", 1))
                                          .build();
        InMemoryConfigStore store = storeWith("{\"broken\":1}");
        Logger logger = Logger.getLogger(InMemoryConfigStore.class.getName());
        TestLogHandler handler = new TestLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        try {
            MigrationExecutionException ex = assertThrows(MigrationExecutionException.class,
                                                          () -> store.read(SimpleConfig.class,
                                                                           migrations,
                                                                           new SimpleConfig()));
            assertTrue(ex.getMessage()
                         .contains("Migration v1 failed while applying set broken.child"));
            assertEquals(0,
                         ex.completedVersionReports()
                           .size());
            assertEquals(MigrationOperationType.SET,
                         ex.failedOperationReport()
                           .type());
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(true);
        }

        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains(
                                           "Migration v1 failed while applying set broken.child")));
    }

    @Test
    void defaultUnknownKeyPolicyPreservesUnknownKeys() {
        InMemoryConfigStore store = storeWith("{\"value\":1,\"unknown\":2,\"_version_\":0}");
        ValueConfig loaded = (ValueConfig) store.read(ValueConfig.class, noMigrations(), new ValueConfig());

        loaded.value = 10;
        store.write(loaded, ValueConfig.class, noMigrations());

        assertTrue(store.readRaw()
                        .contains("\"unknown\":2"), store.readRaw());
    }

    @Test
    void removeUnknownKeyPolicyRemovesUnknownKeysOnWrite() {
        InMemoryConfigStore store = storeWith("{\"value\":1,\"unknown\":2,\"_version_\":0}");
        store.unknownKeyPolicy(UnknownKeyPolicy.REMOVE);
        ValueConfig loaded = (ValueConfig) store.read(ValueConfig.class, noMigrations(), new ValueConfig());

        loaded.value = 10;
        store.write(loaded, ValueConfig.class, noMigrations());

        assertFalse(store.readRaw()
                         .contains("\"unknown\""), store.readRaw());
    }

    @Test
    void failUnknownKeyPolicyRejectsUnknownKeysOnRead() {
        InMemoryConfigStore store = storeWith("{\"value\":1,\"unknown\":2,\"_version_\":0}");
        store.unknownKeyPolicy(UnknownKeyPolicy.FAIL);

        UnknownConfigKeyException ex = assertThrows(UnknownConfigKeyException.class,
                                                    () -> store.read(ValueConfig.class,
                                                                     noMigrations(),
                                                                     new ValueConfig()));

        assertEquals("Unknown config key: unknown", ex.getMessage());
    }

    @Test
    void customUnknownKeyPolicyCanFilterByPath() {
        InMemoryConfigStore store = storeWith("{\"value\":1,\"keepCustom\":2,\"dropCustom\":3,\"_version_\":0}");
        store.unknownKeyPolicy(UnknownKeyPolicy.filter(path -> path.startsWith("keep")));
        ValueConfig loaded = (ValueConfig) store.read(ValueConfig.class, noMigrations(), new ValueConfig());

        loaded.value = 10;
        store.write(loaded, ValueConfig.class, noMigrations());

        assertTrue(store.readRaw()
                        .contains("\"keepCustom\":2"), store.readRaw());
        assertFalse(store.readRaw()
                         .contains("\"dropCustom\""), store.readRaw());
    }

    // ---- history: pushHistory / readHistory ----

    @Test
    void pushHistoryStoresEntry() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(5), ChangeTrace.programmatic());

        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void pushHistoryEmbeddsTimestamp() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        long before = System.currentTimeMillis();
        store.pushHistory(new ValueConfig(0), ChangeTrace.programmatic());
        long after = System.currentTimeMillis();

        HistoryEntry entry = store.readHistory(ValueConfig.class, noMigrations())
                                  .get(0);
        assertTrue(entry.timestamp() >= before && entry.timestamp() <= after);
    }

    @Test
    void readHistoryReturnsNewestFirst() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(1), ChangeTrace.programmatic());
        store.pushHistory(new ValueConfig(2), ChangeTrace.programmatic());

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
            store.pushHistory(new ValueConfig(i), ChangeTrace.programmatic());
        }

        assertEquals(3,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    // ---- history: canRestoreHistoryIndex ----

    @Test
    void canRestoreHistoryIndexReturnsFalseWhenHistoryEmpty() {
        assertFalse(new InMemoryConfigStore(gson).canRestoreHistoryIndex(1));
    }

    @Test
    void canRestoreHistoryIndexReturnsFalseWithSingleEntry() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0), ChangeTrace.programmatic());
        assertFalse(store.canRestoreHistoryIndex(1));
    }

    @Test
    void canRestoreHistoryIndexReturnsTrueWithTwoEntries() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0), ChangeTrace.programmatic());
        store.pushHistory(new ValueConfig(1), ChangeTrace.programmatic());
        assertTrue(store.canRestoreHistoryIndex(1));
    }

    @Test
    void canRestoreHistoryIndexRespectsIndex() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0), ChangeTrace.programmatic());
        store.pushHistory(new ValueConfig(1), ChangeTrace.programmatic());
        // history[2] needs three entries.
        assertFalse(store.canRestoreHistoryIndex(2));
        store.pushHistory(new ValueConfig(2), ChangeTrace.programmatic());
        assertTrue(store.canRestoreHistoryIndex(2));
    }

    // ---- history: restoreHistoryIndex ----

    @Test
    void restoreHistoryIndexRemovesNewerEntriesAndReturnsSelectedEntry() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(10), ChangeTrace.programmatic()); // old
        store.pushHistory(new ValueConfig(20), ChangeTrace.programmatic()); // current

        ValueConfig restored = (ValueConfig) store.restoreHistoryIndex(ValueConfig.class, noMigrations(), 1);

        assertEquals(10, restored.value);
        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void restoreHistoryIndex2() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushHistory(new ValueConfig(0), ChangeTrace.programmatic());
        store.pushHistory(new ValueConfig(10), ChangeTrace.programmatic());
        store.pushHistory(new ValueConfig(20), ChangeTrace.programmatic());
        // history: [20, 10, 0]

        ValueConfig restored = (ValueConfig) store.restoreHistoryIndex(ValueConfig.class, noMigrations(), 2);

        assertEquals(0, restored.value);
        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void pushAuditStoresChanges() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushAudit(new AuditEntry(123L,
                                       new ChangeTrace(ChangeSource.COMMAND,
                                                       new ChangeActor("console", null),
                                                       "set value",
                                                       List.of("value")),
                                       List.of(new AuditChange("value", "10", "20"))));

        AuditEntry entry = store.readAudit()
                                .get(0);
        assertEquals(123L, entry.timestamp());
        assertEquals(ChangeSource.COMMAND,
                     entry.trace()
                          .source());
        assertEquals("value",
                     entry.changes()
                          .get(0)
                          .path());
        assertEquals("10",
                     entry.changes()
                          .get(0)
                          .beforeText());
        assertEquals("20",
                     entry.changes()
                          .get(0)
                          .afterText());
    }

    @Test
    void readAuditAllowsLegacyEntriesWithoutChanges() {
        InMemoryConfigStore store = new InMemoryConfigStore(gson);
        store.pushAudit(new AuditEntry(1L, new ChangeTrace(ChangeSource.PROGRAMMATIC, null, null, List.of("value"))));

        AuditEntry entry = store.readAudit()
                                .get(0);
        assertTrue(entry.changes()
                        .isEmpty());
        assertEquals(List.of("value"),
                     entry.trace()
                          .paths());
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

    static class TestLogHandler extends Handler {
        final List<String> messages = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            messages.add(record.getMessage());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
