package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.exception.InvalidValueException;
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
}
