package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Nullable;
import net.kunmc.lab.configlib.annotation.Range;
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

        assertThrows(LoadingConfigInvalidValueException.class, cfg::loadConfig);
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
}
