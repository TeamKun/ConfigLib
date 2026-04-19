package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


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

    private TestConfig initWithoutBackgroundDetection(TestConfig cfg) {
        TestConfig initialized = init(cfg);
        initialized.timer.cancel();
        return initialized;
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

    // ---- onChange ----

    @Test
    void onChangeNotCalledOnFirstLoad() {
        TestConfig cfg = new TestConfig();
        AtomicInteger count = new AtomicInteger();
        cfg.onChange(count::incrementAndGet);

        init(cfg);

        assertEquals(0, count.get());
    }

    @Test
    void onChangeCalledOnSubsequentLoad() throws LoadingConfigInvalidValueException {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":1,\"_version_\":0}");
        init(cfg);

        AtomicInteger count = new AtomicInteger();
        cfg.onChange(count::incrementAndGet);

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
        cfg.onChange(a::incrementAndGet);
        cfg.onChange(b::incrementAndGet);

        cfg.loadConfig();

        assertEquals(1, a.get());
        assertEquals(1, b.get());
    }

    // ---- 履歴管理 ----

    @Test
    void initialHistoryHasExactlyOneEntry() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        assertEquals(1,
                     cfg.readHistory()
                        .size());
    }

    @Test
    void noAdditionalHistoryPushWhenHistoryAlreadyExists() {
        TestConfig cfg = new TestConfig();
        // 履歴が既存（サーバー再起動前の状態を模擬）
        cfg.store.pushHistory(cfg);
        initWithoutBackgroundDetection(cfg);

        assertEquals(1,
                     cfg.readHistory()
                        .size());
    }

    @Test
    void applyUndoRevertsToHistoricalValue() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig()); // history: [value=0]
        cfg.value = 10;
        cfg.pushHistory();                        // history: [value=10, value=0]

        assertTrue(cfg.applyUndo(1));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoCalledTwiceKeepsGoingBack() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig()); // history: [value=0]
        cfg.value = 10;
        cfg.pushHistory();                        // history: [value=10, value=0]
        cfg.value = 20;
        cfg.pushHistory();                        // history: [value=20, value=10, value=0]

        assertTrue(cfg.applyUndo(1));
        assertEquals(10, cfg.value);

        assertTrue(cfg.applyUndo(1));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoWithStepsBack2() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig()); // history: [value=0]
        cfg.value = 10;
        cfg.pushHistory();                        // history: [value=10, value=0]
        cfg.value = 20;
        cfg.pushHistory();                        // history: [value=20, value=10, value=0]

        assertTrue(cfg.applyUndo(2));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoReturnsFalseWhenOnlyOneHistoryEntry() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig()); // history: [value=0] — 1件のみ
        assertFalse(cfg.applyUndo(1));
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
