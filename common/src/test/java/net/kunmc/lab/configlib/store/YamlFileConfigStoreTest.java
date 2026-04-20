package net.kunmc.lab.configlib.store;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.migration.MigrationContext;
import net.kunmc.lab.configlib.migration.Migrations;
import net.kunmc.lab.configlib.value.IntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class YamlFileConfigStoreTest {
    @TempDir
    File tempDir;

    private File configFile;
    private Gson gson;
    private YamlFileConfigStore store;

    @BeforeEach
    void setUp() {
        configFile = new File(tempDir, "config.yml");
        gson = new Gson();
        store = new YamlFileConfigStore(configFile, gson);
    }

    private Migrations noMigrations() {
        return new Migrations(new TreeMap<>());
    }

    private Migrations migrations(Consumer<TreeMap<Integer, Consumer<MigrationContext>>> setup) {
        TreeMap<Integer, Consumer<MigrationContext>> m = new TreeMap<>();
        setup.accept(m);
        return new Migrations(m);
    }

    private void writeFile(String yaml) throws IOException {
        Files.writeString(configFile.toPath(), yaml, StandardCharsets.UTF_8);
    }

    private String readFile() throws IOException {
        return Files.readString(configFile.toPath(), StandardCharsets.UTF_8);
    }

    @Test
    void writeCreatesYamlFile() throws IOException {
        SimpleConfig cfg = new SimpleConfig();
        cfg.value = 42;

        store.write(cfg);

        assertTrue(configFile.exists());
        assertTrue(readFile().contains("value: 42"), readFile());
    }

    @Test
    void readDeserializesYaml() throws IOException {
        writeFile("value: 99\n_version_: 0\n");

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class, noMigrations(), new SimpleConfig());

        assertEquals(99, loaded.value);
    }

    @Test
    void readAppliesMigrationAndUpdatesYaml() throws IOException {
        writeFile("value: 3\n_version_: 0\n");

        SimpleConfig loaded = (SimpleConfig) store.read(SimpleConfig.class,
                                                        migrations(m -> m.put(1,
                                                                              ctx -> ctx.setInt("value",
                                                                                                ctx.getInt("value") * 10))),
                                                        new SimpleConfig());

        assertEquals(30, loaded.value);
        assertTrue(readFile().contains("_version_: 1"), readFile());
    }

    @Test
    void writeAddsDescriptionCommentForValueFields() throws IOException {
        ValueFieldConfig cfg = new ValueFieldConfig();

        store.write(cfg);

        assertTrue(readFile().contains("# Maximum number of active arenas."), readFile());
        assertTrue(readFile().contains("maxArenas:"), readFile());
        assertFalse(readFile().contains("description:"), readFile());
    }

    @Test
    void writeAddsDescriptionCommentForPojoFields() throws IOException {
        PojoFieldConfig cfg = new PojoFieldConfig();

        store.write(cfg);

        assertTrue(readFile().contains("# Maximum player count."), readFile());
        assertTrue(readFile().contains("maxPlayers: 20"), readFile());
    }

    @Test
    void writeAddsDescriptionCommentForNestedPojoFields() throws IOException {
        NestedPojoConfig cfg = new NestedPojoConfig();

        store.write(cfg);

        assertTrue(readFile().contains("# Arena settings."), readFile());
        assertTrue(readFile().contains("arena:"), readFile());
        assertTrue(readFile().contains("  # Maximum number of arenas."), readFile());
        assertTrue(readFile().contains("  maxArenas: 5"), readFile());
    }

    @Test
    void writeKeepsExternalDiskChangeWhenMemoryDidNotChangeThatField() throws IOException {
        writeFile("value: 1\nother: 2\n_version_: 0\n");
        TwoFieldConfig loaded = (TwoFieldConfig) store.read(TwoFieldConfig.class, noMigrations(), new TwoFieldConfig());

        loaded.value = 10;
        writeFile("value: 1\nother: 20\n_version_: 0\n");
        TwoFieldConfig saved = (TwoFieldConfig) store.write(loaded, TwoFieldConfig.class, noMigrations());

        assertEquals(10, saved.value);
        assertEquals(20, saved.other);
        assertTrue(readFile().contains("value: 10"), readFile());
        assertTrue(readFile().contains("other: 20"), readFile());
    }

    @Test
    void historyAndUndoUseYamlHistoryFile() throws IOException {
        store.pushHistory(new SimpleConfig(10));
        store.pushHistory(new SimpleConfig(20));

        List<HistoryEntry> history = store.readHistory(SimpleConfig.class, noMigrations());
        assertEquals(2, history.size());
        assertEquals(20,
                     ((SimpleConfig) history.get(0)
                                            .config()).value);
        assertTrue(store.canUndo(1));

        SimpleConfig restored = (SimpleConfig) store.undo(SimpleConfig.class, noMigrations(), 1);

        assertEquals(10, restored.value);
        File historyFile = new File(tempDir, "config.history.yml");
        assertTrue(historyFile.exists());
        String historyYaml = Files.readString(historyFile.toPath(), StandardCharsets.UTF_8);
        assertTrue(historyYaml.startsWith("history:"), historyYaml);
    }

    static class SimpleConfig extends CommonBaseConfig {
        int value = 0;

        SimpleConfig() {
        }

        SimpleConfig(int value) {
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

    static class ValueFieldConfig extends CommonBaseConfig {
        public final IntegerValue maxArenas = new IntegerValue(3).description("Maximum number of active arenas.");

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class PojoFieldConfig extends CommonBaseConfig {
        @Description("Maximum player count.")
        public int maxPlayers = 20;

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }
    }

    static class NestedPojoConfig extends CommonBaseConfig {
        @Description("Arena settings.")
        public ArenaSettings arena = new ArenaSettings();

        @Override
        protected ConfigStore createConfigStore() {
            return new InMemoryConfigStore(new Gson());
        }

        static class ArenaSettings {
            @Description("Maximum number of arenas.")
            public int maxArenas = 5;
        }
    }
}
