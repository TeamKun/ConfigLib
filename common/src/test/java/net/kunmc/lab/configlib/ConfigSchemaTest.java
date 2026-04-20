package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Nullable;
import net.kunmc.lab.configlib.annotation.Range;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.StringValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConfigSchemaTest {
    private TestConfig config;

    @BeforeEach
    void setUp() {
        config = new TestConfig();
        config.init(new CommonBaseConfig.Option());
    }

    @Test
    void schemaHasOneEntryPerValueField() {
        assertEquals(2,
                     config.schema()
                           .entries()
                           .size());
    }

    @Test
    void entryPathMatchesFieldName() {
        assertTrue(config.schema()
                         .findEntry("count")
                         .isPresent());
        assertTrue(config.schema()
                         .findEntry("label")
                         .isPresent());
    }

    @Test
    void entryNameUsesValueEntryNameOverride() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        assertEquals("itemCount",
                     entry.get()
                          .entryName());
    }

    @Test
    void schemaSkipsStaticAndTransientValueFields() {
        assertFalse(config.schema()
                          .findEntry("staticValue")
                          .isPresent());
        assertFalse(config.schema()
                          .findEntry("transientValue")
                          .isPresent());
    }

    @Test
    void metadataCarriesDescription() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        assertEquals("Item count.",
                     entry.get()
                          .metadata()
                          .description());
    }

    @Test
    void metadataIsNullWhenNoDescription() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("label");
        assertTrue(entry.isPresent());
        assertNull(entry.get()
                        .metadata()
                        .description());
    }

    @Test
    void accessorReadsCurrentValue() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        assertEquals(5,
                     entry.get()
                          .get());
    }

    @Test
    void accessorWritesValue() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        @SuppressWarnings("unchecked") ConfigSchemaEntry<Integer> typed = (ConfigSchemaEntry<Integer>) entry.get();
        typed.set(42);
        assertEquals(42, config.count.value());
    }

    @Test
    void validatorRejectsInvalidValue() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        @SuppressWarnings("unchecked") ConfigSchemaEntry<Integer> typed = (ConfigSchemaEntry<Integer>) entry.get();
        assertThrows(InvalidValueException.class, () -> typed.validate(-1));
    }

    @Test
    void validatorAcceptsValidValue() {
        Optional<ConfigSchemaEntry<?>> entry = config.schema()
                                                     .findEntry("count");
        assertTrue(entry.isPresent());
        @SuppressWarnings("unchecked") ConfigSchemaEntry<Integer> typed = (ConfigSchemaEntry<Integer>) entry.get();
        assertDoesNotThrow(() -> typed.validate(10));
    }

    @Test
    void findEntryReturnsEmptyForUnknownPath() {
        assertFalse(config.schema()
                          .findEntry("nonexistent")
                          .isPresent());
    }

    @Test
    void schemaIncludesPojoPublicFields() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema()
                      .findEntry("maxPlayers")
                      .isPresent());
        assertTrue(cfg.schema()
                      .findEntry("motd")
                      .isPresent());
    }

    @Test
    void pojoDescriptionAnnotationBecomesMetadata() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        ConfigSchemaEntry<?> entry = cfg.schema()
                                        .findEntry("maxPlayers")
                                        .orElseThrow(AssertionError::new);

        assertEquals("Maximum player count.",
                     entry.metadata()
                          .description());
    }

    @Test
    void pojoRangeAnnotationBecomesValidator() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked") ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                                                          .findEntry(
                                                                                                                  "maxPlayers")
                                                                                                          .orElseThrow(
                                                                                                                  AssertionError::new);

        assertDoesNotThrow(() -> entry.validate(20));
        assertThrows(InvalidValueException.class, () -> entry.validate(101));
    }

    @Test
    void schemaValidationExceptionCarriesPathAndValue() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        ConfigSchemaEntry<?> entry = cfg.schema()
                                        .findEntry("maxPlayers")
                                        .orElseThrow(AssertionError::new);

        ConfigValidationException ex = assertThrows(ConfigValidationException.class,
                                                    () -> ConfigSchemaValidation.validate(entry, 101));

        assertEquals("maxPlayers", ex.path().asString());
        assertEquals(101, ex.value());
        assertTrue(ex.validationCause() instanceof InvalidValueException);
    }

    @Test
    void pojoAccessorReadsAndWritesPublicField() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked") ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                                                          .findEntry(
                                                                                                                  "maxPlayers")
                                                                                                          .orElseThrow(
                                                                                                                  AssertionError::new);
        assertEquals(20, entry.get());

        entry.set(30);

        assertEquals(30, cfg.maxPlayers);
    }

    @Test
    void schemaIncludesPrivateAndPackagePrivatePojoFields() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema()
                      .findEntry("privatePojoField")
                      .isPresent());
        assertTrue(cfg.schema()
                      .findEntry("packagePrivatePojoField")
                      .isPresent());
    }

    @Test
    void schemaSkipsStaticAndTransientPojoFields() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertFalse(cfg.schema()
                       .findEntry("staticPojoField")
                       .isPresent());
        assertFalse(cfg.schema()
                       .findEntry("runtimeCache")
                       .isPresent());
    }

    @Test
    void loadValidatesPojoRangeAnnotation() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"maxPlayers\":101,\"motd\":\"bad\",\"_version_\":0}");

        LoadingConfigInvalidValueException ex = assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);

        assertEquals("maxPlayers", ex.path().asString());
        assertEquals(101, ex.value());
    }

    @Test
    void loadRejectsNullPojoFieldWithoutNullableAnnotation() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"maxPlayers\":20,\"motd\":null,\"_version_\":0}");

        assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);
    }

    @Test
    void loadAllowsNullPojoFieldWithNullableAnnotation() throws LoadingConfigInvalidValueException {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"maxPlayers\":20,\"motd\":\"ok\",\"nullableMotd\":null,\"_version_\":0}");

        cfg.loadConfig();

        assertNull(cfg.nullableMotd);
    }

    @Test
    void loadPassesNullValueToValueValidator() {
        ValueNullConfig cfg = new ValueNullConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"label\":{\"value\":null},\"_version_\":0}");

        assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);
    }

    @Test
    void loadKeepsDefaultForMissingValueField() throws LoadingConfigInvalidValueException {
        ValueNullConfig cfg = new ValueNullConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"_version_\":0}");

        cfg.loadConfig();

        assertEquals("default", cfg.label.value());
    }

    @Test
    void loadKeepsDefaultForMissingPojoField() throws LoadingConfigInvalidValueException {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"maxPlayers\":20,\"_version_\":0}");

        cfg.loadConfig();

        assertEquals("hello", cfg.motd);
    }

    @Test
    void mutationDetectionObservesPrimitivePojoFields() {
        PojoConfig cfg = new PojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        cfg.mutate(() -> cfg.maxPlayers = 30);

        assertTrue(cfg.store.readRaw()
                            .contains("\"maxPlayers\":30"));
    }

    static final class TestConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        public final IntegerValue count = new IntegerValue(5).entryName("itemCount")
                                                             .description("Item count.")
                                                             .addValidator(v -> {
                                                                 if (v < 0) {
                                                                     throw new InvalidValueException(
                                                                             "count must be non-negative");
                                                                 }
                                                             });
        public final StringValue label = new StringValue("default");
        public static final IntegerValue staticValue = new IntegerValue(1);
        public final transient IntegerValue transientValue = new IntegerValue(2);

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static final class PojoConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        @Description("Maximum player count.")
        @Range(min = 1, max = 100)
        public int maxPlayers = 20;
        @Description("Message of the day.")
        public String motd = "hello";
        @Nullable
        public String nullableMotd = "nullable";
        private String privatePojoField = "private";
        String packagePrivatePojoField = "package";
        public static int staticPojoField = 1;
        public transient String runtimeCache = "cache";

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static final class ValueNullConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        public final StringValue label = new StringValue("default").addValidator(v -> {
            if (v == null) {
                throw new InvalidValueException("label must not be null");
            }
        });

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static final class NestedPojoConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        @Description("Arena settings.")
        public ArenaSettings arena = new ArenaSettings();
        public Mode mode = Mode.EASY;

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }

        static final class ArenaSettings {
            @Description("Maximum number of arenas.")
            @Range(min = 1, max = 50)
            public int maxArenas = 5;
            public String name = "default";
            public transient String cache = "ignored";
            public static int staticField = 0;
        }

        enum Mode { EASY, HARD }
    }

    @Test
    void nestedPojoFieldsAppearWithDottedPaths() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema().findEntry("arena.maxArenas").isPresent());
        assertTrue(cfg.schema().findEntry("arena.name").isPresent());
    }

    @Test
    void nestedPojoTransientAndStaticFieldsAreExcluded() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertFalse(cfg.schema().findEntry("arena.cache").isPresent());
        assertFalse(cfg.schema().findEntry("arena.staticField").isPresent());
        assertFalse(cfg.schema().findEntry("arena").isPresent());
    }

    @Test
    void nestedEnumIsLeafNotExpanded() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema().findEntry("mode").isPresent());
        assertFalse(cfg.schema().findEntry("mode.name").isPresent());
        assertFalse(cfg.schema().findEntry("mode.ordinal").isPresent());
    }

    @Test
    void nestedPojoDescriptionAnnotationBecomesMetadata() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        ConfigSchemaEntry<?> entry = cfg.schema()
                                        .findEntry("arena.maxArenas")
                                        .orElseThrow(AssertionError::new);
        assertEquals("Maximum number of arenas.", entry.metadata().description());
    }

    @Test
    void nestedPojoAccessorReadsCurrentValue() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);
        assertEquals(5, entry.get());
    }

    @Test
    void nestedPojoAccessorWritesValue() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);
        entry.set(10);
        assertEquals(10, cfg.arena.maxArenas);
    }

    @Test
    void nestedPojoRangeAnnotationBecomesValidator() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);

        assertDoesNotThrow(() -> entry.validate(10));
        assertThrows(InvalidValueException.class, () -> entry.validate(51));
    }

    @Test
    void nestedPojoLoadRoundTrip() throws LoadingConfigInvalidValueException {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"arena\":{\"maxArenas\":12,\"name\":\"test\"},\"_version_\":0}");

        cfg.loadConfig();

        assertEquals(12, cfg.arena.maxArenas);
        assertEquals("test", cfg.arena.name);
    }

    @Test
    void nestedPojoLoadValidatesRangeAnnotation() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"arena\":{\"maxArenas\":99,\"name\":\"bad\"},\"_version_\":0}");

        LoadingConfigInvalidValueException ex = assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);

        assertEquals("arena.maxArenas", ex.path().asString());
        assertEquals(99, ex.value());
    }

    @Test
    void nestedPojoMutationDetectionSavesChanges() {
        NestedPojoConfig cfg = new NestedPojoConfig();
        cfg.init(new CommonBaseConfig.Option());

        cfg.mutate(() -> cfg.arena.maxArenas = 15);

        assertTrue(cfg.store.readRaw().contains("\"maxArenas\":15"));
    }

    static final class ImmutableNestedConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        public ArenaSettings arena = new ArenaSettings(5, "default");

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }

        static final class ArenaSettings {
            @Description("Maximum number of arenas.")
            @Range(min = 1, max = 50)
            private final int maxArenas;
            private final String name;

            public ArenaSettings(int maxArenas, String name) {
                this.maxArenas = maxArenas;
                this.name = name;
            }

            public int maxArenas() {
                return maxArenas;
            }

            public String name() {
                return name;
            }
        }
    }

    @Test
    void immutableNestedFieldsAppearWithDottedPaths() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema().findEntry("arena.maxArenas").isPresent());
        assertTrue(cfg.schema().findEntry("arena.name").isPresent());
        assertFalse(cfg.schema().findEntry("arena").isPresent());
    }

    @Test
    void immutableNestedDescriptionAnnotationBecomesMetadata() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        ConfigSchemaEntry<?> entry = cfg.schema()
                                        .findEntry("arena.maxArenas")
                                        .orElseThrow(AssertionError::new);
        assertEquals("Maximum number of arenas.", entry.metadata().description());
    }

    @Test
    void immutableNestedAccessorReadsCurrentValue() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);
        assertEquals(5, entry.get());
    }

    @Test
    void immutableNestedSetReconstructsAndPreservesOtherFields() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);
        entry.set(10);

        assertEquals(10, cfg.arena.maxArenas());
        assertEquals("default", cfg.arena.name());
    }

    @Test
    void immutableNestedRangeAnnotationBecomesValidator() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("arena.maxArenas")
                                                                           .orElseThrow(AssertionError::new);

        assertDoesNotThrow(() -> entry.validate(10));
        assertThrows(InvalidValueException.class, () -> entry.validate(51));
    }

    @Test
    void immutableNestedLoadRoundTrip() throws LoadingConfigInvalidValueException {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"arena\":{\"maxArenas\":12,\"name\":\"test\"},\"_version_\":0}");

        cfg.loadConfig();

        assertEquals(12, cfg.arena.maxArenas());
        assertEquals("test", cfg.arena.name());
    }

    @Test
    void immutableNestedLoadValidatesRangeAnnotation() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"arena\":{\"maxArenas\":99,\"name\":\"bad\"},\"_version_\":0}");

        assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);
    }

    @Test
    void immutableNestedMutationDetectionSavesChanges() {
        ImmutableNestedConfig cfg = new ImmutableNestedConfig();
        cfg.init(new CommonBaseConfig.Option());

        cfg.mutate(() -> cfg.arena = new ImmutableNestedConfig.ArenaSettings(15, "arena"));

        assertTrue(cfg.store.readRaw().contains("\"maxArenas\":15"));
    }

    // --- deep nesting (class-within-class) ---

    static final class DeepMutableConfig extends CommonBaseConfig {
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        public Outer outer = new Outer();

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }

        static final class Outer {
            public Inner inner = new Inner();
            public int x = 1;

            static final class Inner {
                public int y = 5;
                public String z = "hello";
            }
        }
    }

    @Test
    void deepMutableNestingFieldsAppearWithDottedPaths() {
        DeepMutableConfig cfg = new DeepMutableConfig();
        cfg.init(new CommonBaseConfig.Option());

        assertTrue(cfg.schema().findEntry("outer.inner.y").isPresent());
        assertTrue(cfg.schema().findEntry("outer.inner.z").isPresent());
        assertTrue(cfg.schema().findEntry("outer.x").isPresent());
        assertFalse(cfg.schema().findEntry("outer.inner").isPresent());
        assertFalse(cfg.schema().findEntry("outer").isPresent());
    }

    @Test
    void deepMutableNestingGetReadsCorrectValue() {
        DeepMutableConfig cfg = new DeepMutableConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("outer.inner.y")
                                                                           .orElseThrow(AssertionError::new);
        assertEquals(5, entry.get());
    }

    @Test
    void deepMutableNestingSetPropagatesToRoot() {
        DeepMutableConfig cfg = new DeepMutableConfig();
        cfg.init(new CommonBaseConfig.Option());

        @SuppressWarnings("unchecked")
        ConfigSchemaEntry<Integer> entry = (ConfigSchemaEntry<Integer>) cfg.schema()
                                                                           .findEntry("outer.inner.y")
                                                                           .orElseThrow(AssertionError::new);
        entry.set(99);

        assertEquals(99, cfg.outer.inner.y);
        assertEquals("hello", cfg.outer.inner.z);
        assertEquals(1, cfg.outer.x);
    }

    @Test
    void deepMutableNestingLoadRoundTrip() throws LoadingConfigInvalidValueException {
        DeepMutableConfig cfg = new DeepMutableConfig();
        cfg.init(new CommonBaseConfig.Option());
        cfg.store.writeRaw("{\"outer\":{\"inner\":{\"y\":42,\"z\":\"world\"},\"x\":7},\"_version_\":0}");

        cfg.loadConfig();

        assertEquals(42, cfg.outer.inner.y);
        assertEquals("world", cfg.outer.inner.z);
        assertEquals(7, cfg.outer.x);
    }
}
