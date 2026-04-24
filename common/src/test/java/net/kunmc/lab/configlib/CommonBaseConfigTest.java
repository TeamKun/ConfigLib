package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.configlib.annotation.ConfigNullable;
import net.kunmc.lab.configlib.annotation.Masked;
import net.kunmc.lab.configlib.store.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CommonBaseConfigTest {
    private CommonBaseConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    private <T extends CommonBaseConfig> T init(T cfg) {
        config = cfg;
        cfg.init(new CommonBaseConfig.Option());
        return cfg;
    }

    private <T extends CommonBaseConfig> T init(T cfg, CommonBaseConfig.Option option) {
        config = cfg;
        cfg.init(option);
        return cfg;
    }

    private <T extends CommonBaseConfig> T initWithoutBackgroundDetection(T cfg) {
        T initialized = init(cfg);
        initialized.timer.cancel();
        return initialized;
    }

    private <T extends CommonBaseConfig> T initWithoutBackgroundDetection(T cfg, CommonBaseConfig.Option option) {
        T initialized = init(cfg, option);
        initialized.timer.cancel();
        return initialized;
    }

    @Test
    void savesDefaultsWhenStoreIsEmpty() {
        TestConfig cfg = init(new TestConfig());

        assertTrue(cfg.store.exists());
        String raw = cfg.store.readRaw();
        assertTrue(raw.contains("\"value\":0"), raw);
        assertTrue(raw.contains("\"count\":10"), raw);
        assertFalse(raw.contains("\"str\""), raw);
        assertNull(cfg.str);
    }

    @Test
    void capturesNullDefaultValue() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());

        assertNull(cfg.str);
    }

    @Test
    void resetAllEntriesToDefaultRestoresNullDefaultValue() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.str = "changed";

        cfg.resetAllEntriesToDefault();

        assertNull(cfg.str);
    }

    @Test
    void loadsValuesFromExistingStore() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":42,\"count\":99,\"str\":\"hoge\",\"_version_\":0}");
        init(cfg);

        assertEquals(42, cfg.value);
        assertEquals(99, cfg.count);
        assertEquals("hoge", cfg.str);
    }

    @Test
    void doesNotOverwriteExistingStoreOnInit() {
        TestConfig cfg = new TestConfig();
        String original = "{\"value\":7,\"count\":8,\"str\":\"hoge\",\"_version_\":0}";
        cfg.store.writeRaw(original);
        init(cfg);

        assertEquals(original, cfg.store.readRaw());
    }

    @Test
    void versionWrittenAsLatestOnFreshInstall() {
        TestConfig cfg = new TestConfig();
        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migrateTo(1, migration -> {
        });
        opt.migrateTo(2, migration -> {
        });
        init(cfg, opt);

        assertTrue(cfg.store.readRaw()
                            .contains("\"_version_\":2"), cfg.store.readRaw());
    }

    @Test
    void migrationAppliedOnLoad() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":5,\"_version_\":0}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migrateTo(1, migration -> migration.convert("value", Integer.class, Integer.class, value -> value * 10));
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
        opt.migrateTo(1, migration -> migration.convert("value", Integer.class, Integer.class, value -> value * 10));
        init(cfg, opt);

        assertEquals(5, cfg.value);
    }

    @Test
    void partialMigrationAppliesOnlyPendingVersions() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":1,\"_version_\":1}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migrateTo(1, migration -> migration.convert("value", Integer.class, Integer.class, value -> value + 100));
        opt.migrateTo(2, migration -> migration.convert("value", Integer.class, Integer.class, value -> value * 10));
        init(cfg, opt);

        assertEquals(10, cfg.value);
    }

    @Test
    void onChangeNotCalledOnFirstLoad() {
        TestConfig cfg = new TestConfig();
        AtomicInteger count = new AtomicInteger();
        cfg.onChange(count::incrementAndGet);

        init(cfg);

        assertEquals(0, count.get());
    }

    @Test
    void onChangeCalledOnSubsequentLoad() {
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
    void multipleOnReloadListenersAllCalled() {
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

    @Test
    void initialHistoryHasExactlyOneEntry() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        assertEquals(1,
                     cfg.readHistory()
                        .size());
        assertEquals(ChangeSource.INITIAL,
                     cfg.readHistory()
                        .get(0)
                        .source());
    }

    @Test
    void noAdditionalHistoryPushWhenHistoryAlreadyExists() {
        TestConfig cfg = new TestConfig();
        cfg.store.pushHistory(cfg, ChangeTrace.programmatic());
        initWithoutBackgroundDetection(cfg);

        assertEquals(1,
                     cfg.readHistory()
                        .size());
    }

    @Test
    void initialHistoryUsesMigrationSourceWhenStoreWasMigratedOnLoad() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":5,\"_version_\":0}");

        CommonBaseConfig.Option opt = new CommonBaseConfig.Option();
        opt.migrateTo(1, migration -> migration.convert("value", Integer.class, Integer.class, value -> value * 10));
        initWithoutBackgroundDetection(cfg, opt);

        assertEquals(1,
                     cfg.readHistory()
                        .size());
        assertEquals(ChangeSource.MIGRATION,
                     cfg.readHistory()
                        .get(0)
                        .source());
    }

    @Test
    void manualPushHistoryUsesProgrammaticSource() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.value = 10;
        cfg.pushHistory();

        assertEquals(ChangeSource.PROGRAMMATIC,
                     cfg.readHistory()
                        .get(0)
                        .source());
    }

    @Test
    void commandMutationStoresActorAwareAuditEntry() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.mutate(() -> cfg.value = 15,
                   new net.kunmc.lab.configlib.store.ChangeTrace(ChangeSource.COMMAND,
                                                                 new net.kunmc.lab.configlib.store.ChangeActor("console",
                                                                                                               null),
                                                                 "set value",
                                                                 java.util.List.of("value")));

        assertEquals(ChangeSource.COMMAND,
                     cfg.readAudit()
                        .get(0)
                        .trace()
                        .source());
        assertEquals("console",
                     cfg.readAudit()
                        .get(0)
                        .trace()
                        .actor()
                        .name());
        assertEquals(java.util.List.of("value"),
                     cfg.readAudit()
                        .get(0)
                        .trace()
                        .paths());
        assertEquals(new AuditChange("value", "0", "15").path(),
                     cfg.readAudit()
                        .get(0)
                        .changes()
                        .get(0)
                        .path());
        assertEquals("0",
                     cfg.readAudit()
                        .get(0)
                        .changes()
                        .get(0)
                        .beforeText());
        assertEquals("15",
                     cfg.readAudit()
                        .get(0)
                        .changes()
                        .get(0)
                        .afterText());
    }

    @Test
    void maskedFieldStoresRawAuditText() {
        MaskedConfig cfg = initWithoutBackgroundDetection(new MaskedConfig());
        cfg.mutate(() -> cfg.secret = "updated");

        assertEquals("initial",
                     cfg.readAudit()
                        .get(0)
                        .findChange("secret")
                        .orElseThrow()
                        .beforeText());
        assertEquals("updated",
                     cfg.readAudit()
                        .get(0)
                        .findChange("secret")
                        .orElseThrow()
                        .afterText());
    }

    @Test
    void applyUndoRevertsToHistoricalValue() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.value = 10;
        cfg.pushHistory();

        assertTrue(cfg.applyUndo(1));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoCalledTwiceKeepsGoingBack() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.value = 10;
        cfg.pushHistory();
        cfg.value = 20;
        cfg.pushHistory();

        assertTrue(cfg.applyUndo(1));
        assertEquals(10, cfg.value);

        assertTrue(cfg.applyUndo(1));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoWithHistoryIndex2() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        cfg.value = 10;
        cfg.pushHistory();
        cfg.value = 20;
        cfg.pushHistory();

        assertTrue(cfg.applyUndo(2));
        assertEquals(0, cfg.value);
    }

    @Test
    void applyUndoReturnsFalseWhenOnlyOneHistoryEntry() {
        TestConfig cfg = initWithoutBackgroundDetection(new TestConfig());
        assertFalse(cfg.applyUndo(1));
    }

    @Test
    void saveConfigIfAbsentDoesNothingWhenStoreExists() {
        TestConfig cfg = new TestConfig();
        cfg.store.writeRaw("{\"value\":99,\"count\":88,\"_version_\":0}");
        init(cfg);

        cfg.value = 0;
        cfg.saveConfigIfAbsent();

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

    static class TestConfig extends CommonBaseConfig {
        int value = 0;
        int count = 10;
        @ConfigNullable
        String str = null;

        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static class MaskedConfig extends CommonBaseConfig {
        @Masked
        String secret = "initial";

        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }
}
