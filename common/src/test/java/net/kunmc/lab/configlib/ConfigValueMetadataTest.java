package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import net.kunmc.lab.configlib.value.IntegerValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigValueMetadataTest {
    @Test
    void loadKeepsCurrentValueDescriptionWhenStoredValueOmitsDescription() throws Exception {
        TestConfig config = new TestConfig();
        config.store.writeRaw("{\"maxArenas\":{\"value\":9},\"_version_\":0}");

        config.init(new CommonBaseConfig.Option());

        assertEquals(9, config.maxArenas.value());
        assertEquals("Maximum number of active arenas.", config.maxArenas.description());
    }

    static final class TestConfig extends CommonBaseConfig {
        final InMemoryConfigStore store = new InMemoryConfigStore(new Gson());
        public final IntegerValue maxArenas = new IntegerValue(3).description("Maximum number of active arenas.");

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }
}
