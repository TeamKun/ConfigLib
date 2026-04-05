package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonBaseConfigTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    private TestConfig init(TestConfig cfg) {
        config = cfg;
        cfg.init(new CommonBaseConfig.Option());
        return cfg;
    }

    private TestConfig init(TestConfig cfg, CommonBaseConfig.Option option) {
        config = cfg;
        cfg.init(option);
        return cfg;
    }

    // ---- 初期化 ----

    @Test
    void savesDefaultsWhenStoreIsEmpty() {
        TestConfig cfg = init(new TestConfig());

        assertTrue(cfg.store.exists());
        String raw = cfg.store.readRaw();
        assertTrue(raw.contains("\"value\":0"), raw);
        assertTrue(raw.contains("\"count\":10"), raw);
    }

    @Test
    void loadsValuesFromExistingStore() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":42,\"count\":99,\"_version_\":0}");
        init(cfg);

        assertEquals(42, cfg.value);
        assertEquals(99, cfg.count);
    }

    @Test
    void doesNotOverwriteExistingStoreOnInit() {
        TestConfig cfg = new TestConfig();
        String original = "{\"value\":7,\"count\":8,\"_version_\":0}";
        cfg.store.writeRaw(original);
        init(cfg);

        assertEquals(original, cfg.store.readRaw());
    }

    @Test
    void versionWrittenAsLatestOnFreshInstall() {
        TestConfig cfg = new TestConfig();
        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migration(1, ctx -> {
        });
        opt.migration(2, ctx -> {
        });
        init(cfg, opt);

        assertTrue(cfg.store.readRaw()
                            .contains("\"_version_\":2"), cfg.store.readRaw());
    }

    // ---- マイグレーション ----

    @Test
    void migrationAppliedOnLoad() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":5,\"_version_\":0}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migration(1, ctx -> ctx.setInt("value", ctx.getInt("value") * 10));
        init(cfg, opt);

        assertEquals(50, cfg.value);
        assertTrue(cfg.store.readRaw()
                            .contains("\"_version_\":1"), cfg.store.readRaw());
    }

    @Test
    void migrationSkippedWhenAlreadyLatest() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":5,\"_version_\":1}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migration(1, ctx -> ctx.setInt("value", ctx.getInt("value") * 10));
        init(cfg, opt);

        assertEquals(5, cfg.value);
    }

    @Test
    void partialMigrationAppliesOnlyPendingVersions() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":1,\"_version_\":1}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migration(1, ctx -> ctx.setInt("value", ctx.getInt("value") + 100)); // スキップ
        opt.migration(2, ctx -> ctx.setInt("value", ctx.getInt("value") * 10)); // 適用
        init(cfg, opt);

        assertEquals(10, cfg.value);
    }

    // ---- onReload ----

    @Test
    void onReloadNotCalledOnFirstLoad() {
        TestConfig cfg = new TestConfig();
        AtomicInteger count = new AtomicInteger();
        cfg.onReload(count::incrementAndGet);

        init(cfg);

        assertEquals(0, count.get());
    }

    @Test
    void onReloadCalledOnSubsequentLoad() throws LoadingConfigInvalidValueException {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":1,\"_version_\":0}");
        init(cfg);

        AtomicInteger count = new AtomicInteger();
        cfg.onReload(count::incrementAndGet);

        cfg.store.writeRaw("{\"value\":2,\"_version_\":0}");
        cfg.loadConfig();

        assertEquals(1, count.get());
        assertEquals(2, cfg.value);
    }

    @Test
    void multipleOnReloadListenersAllCalled() throws LoadingConfigInvalidValueException {
        TestConfig cfg = new TestConfig();
        init(cfg);

        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        cfg.onReload(a::incrementAndGet);
        cfg.onReload(b::incrementAndGet);

        cfg.loadConfig();

        assertEquals(1, a.get());
        assertEquals(1, b.get());
    }

    // ---- saveConfigIfAbsent / Present ----

    @Test
    void saveConfigIfAbsentDoesNothingWhenStoreExists() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":99,\"count\":88,\"_version_\":0}");
        init(cfg);

        cfg.value = 0;
        cfg.saveConfigIfAbsent();

        // 上書きされていない
        assertTrue(cfg.store.readRaw()
                            .contains("\"value\":99"), cfg.store.readRaw());
    }

    @Test
    void saveConfigIfPresentSavesCurrentState() {
        TestConfig cfg = new TestConfig();
        init(cfg);

        cfg.value = 42;
        cfg.saveConfigIfPresent();

        assertTrue(cfg.store.readRaw()
                            .contains("\"value\":42"), cfg.store.readRaw());
    }

    // ---- TestConfig ----

    static class TestConfig extends CommonBaseConfig {
        // String フィールドは ConfigUtil.replaceFields が String 内部に再帰するため使用しない
        int value = 0;
        int count = 10;

        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }
}
