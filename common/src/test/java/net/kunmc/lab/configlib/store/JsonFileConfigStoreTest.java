package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
        return new Migrations(new TreeMap<>());
    }

    private Migrations migrations(Consumer<TreeMap<Integer, Consumer<MigrationContext>>> setup) {
        TreeMap<Integer, Consumer<MigrationContext>> m = new TreeMap<>();
        setup.accept(m);
        return new Migrations(m);
    }

    private void writeFile(String json) throws IOException {
        Files.write(configFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
    }

    private String readFile() throws IOException {
        return new String(Files.readAllBytes(configFile.toPath()), StandardCharsets.UTF_8);
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

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class, noMigrations());

        assertEquals(99, loaded.value);
    }

    @Test
    void readAppliesMigrationAndUpdatesFile() throws IOException {
        writeFile("{\"value\":3,\"_version_\":0}");

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class,
                                                        migrations(m -> m.put(1,
                                                                              ctx -> ctx.setInt("value",
                                                                                                ctx.getInt("value") * 10))));

        assertEquals(30, loaded.value);
        assertTrue(readFile().contains("\"_version_\":1"), readFile());
    }

    @Test
    void readDoesNotModifyFileWhenNoMigrationNeeded() throws IOException {
        String original = "{\"value\":5,\"_version_\":1}";
        writeFile(original);

        store.read(SimpleConfig.class,
                   migrations(m -> m.put(1, ctx -> ctx.setInt("value", ctx.getInt("value") + 100))));

        assertEquals(original, readFile());
    }

    // ---- startWatching() ----

    @Test
    void startWatchingCanBeClosedWithoutError() throws IOException {
        store.write(new SimpleConfig()); // ディレクトリ確保
        Timer timer = new Timer();
        try {
            assertDoesNotThrow(() -> store.startWatching(timer, () -> {
                                          })
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
            store.startWatching(timer, () -> called.set(true));

            // ファイルを変更してウォッチャーが検知するのを待つ
            Thread.sleep(200);
            Files.write(configFile.toPath(), "{\"value\":1}".getBytes(StandardCharsets.UTF_8));
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
                                                                                            noMigrations());

        assertEquals(3, loaded.point.x);
        assertEquals(7, loaded.point.y);
    }

    // ---- シリアライズ: ジェネリクスを持つオブジェクト ----

    @Test
    void writeAndReadGenericListField() {
        GenericConfig cfg = new GenericConfig();
        cfg.tags = Arrays.asList("alpha", "beta", "gamma");
        store.write(cfg);

        GenericConfig loaded = (GenericConfig) new JsonFileConfigStore(configFile, gson).read(GenericConfig.class,
                                                                                              noMigrations());

        assertEquals(Arrays.asList("alpha", "beta", "gamma"), loaded.tags);
    }

    @Test
    void writeAndReadGenericMapField() {
        MapConfig cfg = new MapConfig();
        cfg.scores = new java.util.LinkedHashMap<>();
        cfg.scores.put("alice", 100);
        cfg.scores.put("bob", 42);
        store.write(cfg);

        MapConfig loaded = (MapConfig) new JsonFileConfigStore(configFile, gson).read(MapConfig.class, noMigrations());

        assertEquals(100, (int) loaded.scores.get("alice"));
        assertEquals(42, (int) loaded.scores.get("bob"));
    }

    // ---- SimpleConfig / ObjectConfig / GenericConfig / MapConfig ----

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
        List<String> tags = Arrays.asList();

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
}
