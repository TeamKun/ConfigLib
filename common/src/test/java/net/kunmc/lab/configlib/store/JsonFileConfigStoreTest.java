package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.MigrationExecutionException;
import net.kunmc.lab.configlib.migration.MigrationOperationType;
import net.kunmc.lab.configlib.migration.Migrations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileConfigStoreTest {
    @TempDir
    File tempDir;

    private File configFile;
    private Gson gson;
    private JsonFileConfigStore store;

    @BeforeEach
    void setUp() {
        configFile = new File(tempDir, "config.json");
        gson = new Gson();
        store = new JsonFileConfigStore(configFile, gson);
    }

    private Migrations noMigrations() {
        return Migrations.empty();
    }

    private void writeFile(String json) throws IOException {
        Files.writeString(configFile.toPath(), json, StandardCharsets.UTF_8);
    }

    private String readFile() throws IOException {
        return Files.readString(configFile.toPath(), StandardCharsets.UTF_8);
    }

    // ---- exists() ----

    @Test
    void existsReturnsFalseWhenFileAbsent() {
        assertFalse(store.exists());
    }

    @Test
    void existsReturnsTrueAfterWrite() {
        store.write(new SimpleConfig());
        assertTrue(store.exists());
    }

    // ---- write() ----

    @Test
    void writeCreatesFile() {
        store.write(new SimpleConfig());
        assertTrue(configFile.exists());
    }

    @Test
    void writeSerializesConfig() throws IOException {
        SimpleConfig cfg = new SimpleConfig();
        cfg.value = 42;
        store.write(cfg);

        assertTrue(readFile().contains("\"value\":42"), readFile());
    }

    @Test
    void writeCreatesParentDirectories() {
        File nested = new File(tempDir, "a/b/c/config.json");
        new JsonFileConfigStore(nested, gson).write(new SimpleConfig());

        assertTrue(nested.exists());
    }

    @Test
    void writeOverwritesExistingFile() throws IOException {
        store.write(new SimpleConfig()); // value=0

        SimpleConfig cfg = new SimpleConfig();
        cfg.value = 99;
        store.write(cfg);

        assertTrue(readFile().contains("\"value\":99"), readFile());
    }

    // ---- read() ----

    @Test
    void readDeserializesConfig() throws IOException {
        writeFile("{\"value\":99,\"_version_\":0}");

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class, noMigrations(), new SimpleConfig());

        assertEquals(99, loaded.value);
    }

    @Test
    void readAppliesMigrationAndUpdatesFile() throws IOException {
        writeFile("{\"value\":3,\"_version_\":0}");

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class,
                                                        Migrations.builder()
                                                                  .migrateTo(1,
                                                                             migration -> migration.convert("value",
                                                                                                            Integer.class,
                                                                                                            Integer.class,
                                                                                                            value -> value * 10))
                                                                  .build(),
                                                        new SimpleConfig());

        assertEquals(30, loaded.value);
        assertTrue(readFile().contains("\"_version_\":1"), readFile());
    }

    @Test
    void readDoesNotModifyFileWhenNoMigrationNeeded() throws IOException {
        String original = "{\"value\":5,\"_version_\":1}";
        writeFile(original);

        store.read(SimpleConfig.class,
                   Migrations.builder()
                             .migrateTo(1,
                                        migration -> migration.convert("value",
                                                                       Integer.class,
                                                                       Integer.class,
                                                                       value -> value + 100))
                             .build(),
                   new SimpleConfig());

        assertEquals(original, readFile());
    }

    @Test
    void readLogsAppliedMigrationSummary() throws IOException {
        writeFile("{\"value\":3,\"_version_\":0}");
        Logger logger = Logger.getLogger(FileConfigStore.class.getName());
        TestLogHandler handler = new TestLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        try {
            store.read(SimpleConfig.class,
                       Migrations.builder()
                                 .migrateTo(1,
                                            migration -> migration.convert("value",
                                                                           Integer.class,
                                                                           Integer.class,
                                                                           value -> value * 10))
                                 .build(),
                       new SimpleConfig());
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(true);
        }

        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains(
                                           "Applied config migrations for config.json from v0 to v1")));
        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains("v1: convert value")));
    }

    @Test
    void readLogsMigrationFailureDetails() throws IOException {
        writeFile("{\"broken\":1,\"_version_\":0}");
        Logger logger = Logger.getLogger(FileConfigStore.class.getName());
        TestLogHandler handler = new TestLogHandler();
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);

        try {
            MigrationExecutionException ex = assertThrows(MigrationExecutionException.class,
                                                          () -> store.read(SimpleConfig.class,
                                                                           Migrations.builder()
                                                                                     .migrateTo(1,
                                                                                                migration -> migration.set(
                                                                                                        "broken.child",
                                                                                                        1))
                                                                                     .build(),
                                                                           new SimpleConfig()));
            assertTrue(ex.getMessage()
                         .contains("Migration v1 failed while applying set broken.child"));
            assertEquals(MigrationOperationType.SET,
                         ex.failedOperationReport()
                           .type());
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(true);
        }

        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains("Config migration failed for config.json")));
        assertTrue(handler.messages.stream()
                                   .anyMatch(message -> message.contains(
                                           "Migration v1 failed while applying set broken.child")));
    }

    @Test
    void writeKeepsExternalDiskChangeWhenMemoryDidNotChangeThatField() throws IOException {
        writeFile("{\"value\":1,\"other\":2,\"_version_\":0}");
        TwoFieldConfig loaded = (TwoFieldConfig) store.read(TwoFieldConfig.class, noMigrations(), new TwoFieldConfig());

        loaded.value = 10;
        writeFile("{\"value\":1,\"other\":20,\"_version_\":0}");
        TwoFieldConfig saved = (TwoFieldConfig) store.write(loaded, TwoFieldConfig.class, noMigrations());

        assertEquals(10, saved.value);
        assertEquals(20, saved.other);
        assertTrue(readFile().contains("\"value\":10"), readFile());
        assertTrue(readFile().contains("\"other\":20"), readFile());
    }

    @Test
    void writeLetsDiskWinWhenSameFieldChangedInMemoryAndOnDisk() throws IOException {
        writeFile("{\"value\":1,\"other\":2,\"_version_\":0}");
        TwoFieldConfig loaded = (TwoFieldConfig) store.read(TwoFieldConfig.class, noMigrations(), new TwoFieldConfig());

        loaded.value = 10;
        writeFile("{\"value\":30,\"other\":2,\"_version_\":0}");
        TwoFieldConfig saved = (TwoFieldConfig) store.write(loaded, TwoFieldConfig.class, noMigrations());

        assertEquals(30, saved.value);
        assertEquals(2, saved.other);
        assertTrue(readFile().contains("\"value\":30"), readFile());
    }

    @Test
    void writePersistsNewFieldAddedAfterStoredConfigWasCreated() throws IOException {
        writeFile("{\"value\":1,\"_version_\":0}");
        AddedFieldConfig loaded = (AddedFieldConfig) store.read(AddedFieldConfig.class,
                                                                noMigrations(),
                                                                new AddedFieldConfig());

        loaded.added = 10;
        AddedFieldConfig saved = (AddedFieldConfig) store.write(loaded, AddedFieldConfig.class, noMigrations());

        assertEquals(10, saved.added);
        assertTrue(readFile().contains("\"added\":10"), readFile());
    }

    // ---- startWatching() ----

    @Test
    void startWatchingCanBeClosedWithoutError() throws IOException {
        store.write(new SimpleConfig()); // ディレクトリ確保
        Timer timer = new Timer();
        try {
            assertDoesNotThrow(() -> store.startWatching(timer, () -> {
                                          }, 100)
                                          .close());
        } finally {
            timer.cancel();
        }
    }

    @Test
    void startWatchingCallsCallbackOnFileChange() throws IOException, InterruptedException {
        store.write(new SimpleConfig());
        AtomicBoolean called = new AtomicBoolean(false);
        Timer timer = new Timer();

        try {
            store.startWatching(timer, () -> called.set(true), 100);

            // ファイルを変更してウォッチャーが検知するのを待つ
            Thread.sleep(200);
            Files.writeString(configFile.toPath(), "{\"value\":1}", StandardCharsets.UTF_8);
            Thread.sleep(1000); // WatchTask は 500ms ごとに実行

            assertTrue(called.get());
        } finally {
            timer.cancel();
        }
    }

    // ---- シリアライズ: ネストオブジェクト ----

    @Test
    void writeAndReadNestedObject() {
        ObjectConfig cfg = new ObjectConfig();
        cfg.point = new Point(3, 7);
        store.write(cfg);

        ObjectConfig loaded = (ObjectConfig) new JsonFileConfigStore(configFile, gson).read(ObjectConfig.class,
                                                                                            noMigrations(),
                                                                                            new ObjectConfig());

        assertEquals(3, loaded.point.x);
        assertEquals(7, loaded.point.y);
    }

    // ---- シリアライズ: ジェネリクスを持つオブジェクト ----

    @Test
    void writeAndReadGenericListField() {
        GenericConfig cfg = new GenericConfig();
        cfg.tags = List.of("alpha", "beta", "gamma");
        store.write(cfg);

        GenericConfig loaded = (GenericConfig) new JsonFileConfigStore(configFile, gson).read(GenericConfig.class,
                                                                                              noMigrations(),
                                                                                              new GenericConfig());

        assertEquals(List.of("alpha", "beta", "gamma"), loaded.tags);
    }

    @Test
    void writeAndReadGenericMapField() {
        MapConfig cfg = new MapConfig();
        cfg.scores = new java.util.LinkedHashMap<>();
        cfg.scores.put("alice", 100);
        cfg.scores.put("bob", 42);
        store.write(cfg);

        MapConfig loaded = (MapConfig) new JsonFileConfigStore(configFile, gson).read(MapConfig.class,
                                                                                      noMigrations(),
                                                                                      new MapConfig());

        assertEquals(100, (int) loaded.scores.get("alice"));
        assertEquals(42, (int) loaded.scores.get("bob"));
    }

    // ---- history: pushHistory / readHistory ----

    @Test
    void pushHistoryStoresEntry() {
        store.pushHistory(new ValueConfig(5));

        assertEquals(1,
                     store.readHistory(ValueConfig.class, noMigrations())
                          .size());
    }

    @Test
    void pushHistoryEmbeddsTimestamp() {
        long before = System.currentTimeMillis();
        store.pushHistory(new ValueConfig(0));
        long after = System.currentTimeMillis();

        HistoryEntry entry = store.readHistory(ValueConfig.class, noMigrations())
                                  .get(0);
        assertTrue(entry.timestamp() >= before && entry.timestamp() <= after);
    }

    @Test
    void readHistoryReturnsNewestFirst() {
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
        JsonFileConfigStore capped = new JsonFileConfigStore(configFile, gson, Throwable::printStackTrace, 3);
        for (int i = 0; i < 5; i++) {
            capped.pushHistory(new ValueConfig(i));
        }

        assertEquals(3,
                     capped.readHistory(ValueConfig.class, noMigrations())
                           .size());
    }

    // ---- history: canUndo ----

    @Test
    void canUndoReturnsFalseWhenHistoryFileAbsent() {
        assertFalse(store.canUndo(1));
    }

    @Test
    void canUndoReturnsFalseWithSingleEntry() {
        store.pushHistory(new ValueConfig(0));
        assertFalse(store.canUndo(1));
    }

    @Test
    void canUndoReturnsTrueWithTwoEntries() {
        store.pushHistory(new ValueConfig(0));
        store.pushHistory(new ValueConfig(1));
        assertTrue(store.canUndo(1));
    }

    @Test
    void canUndoRespectsStepsBack() {
        store.pushHistory(new ValueConfig(0));
        store.pushHistory(new ValueConfig(1));
        assertFalse(store.canUndo(2));
        store.pushHistory(new ValueConfig(2));
        assertTrue(store.canUndo(2));
    }

    // ---- history: undo ----

    @Test
    void undoRemovesTopAndReturnsNewTop() {
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

    // ---- SimpleConfig / ObjectConfig / GenericConfig / MapConfig / ValueConfig ----

    static class SimpleConfig extends CommonBaseConfig {
        int value = 0;

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class ObjectConfig extends CommonBaseConfig {
        Point point = new Point(0, 0);

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class GenericConfig extends CommonBaseConfig {
        List<String> tags = List.of();

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class MapConfig extends CommonBaseConfig {
        Map<String, Integer> scores = new java.util.LinkedHashMap<>();

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

    static class TwoFieldConfig extends CommonBaseConfig {
        int value;
        int other;

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class AddedFieldConfig extends CommonBaseConfig {
        int value;
        int added = 1;

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
