package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Range;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.collection.StringListValue;
import net.kunmc.lab.configlib.value.map.String2IntegerMapValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.initConfig;
import static net.kunmc.lab.configlib.ConfigCommandTestSupport.messages;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigCommandMessageSnapshotTest {
    private CommonBaseConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void valueCommandSuccessMessagesMatchSnapshot() {
        SnapshotConfig cfg = initConfig(new SnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config count", sender);
            tester.execute("config count 25", sender);
            tester.execute("config count set 35", sender);
            tester.execute("config count inc", sender);
            tester.execute("config count dec 5", sender);
            tester.execute("config count reset", sender);

            tester.execute("config names", sender);
            tester.execute("config names add alex", sender);
            tester.execute("config names add steve", sender);
            tester.execute("config names remove alex", sender);
            tester.execute("config names clear", sender);
            tester.execute("config names reset", sender);

            tester.execute("config scores", sender);
            tester.execute("config scores put \"alice\" 7", sender);
            tester.execute("config scores put \"bob\" 3", sender);
            tester.execute("config scores remove alice", sender);
            tester.execute("config scores clear", sender);
            tester.execute("config scores reset", sender);
        }

        SnapshotAssertions.assertMatchesSnapshot("config-command-success-messages.txt", messages(sender));
    }

    @Test
    void valueCommandValidationFailureMessagesMatchSnapshot() {
        ValidatedSnapshotConfig cfg = initConfig(new ValidatedSnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config count 13", sender);
            tester.execute("config count inc 3", sender);
            cfg.count.value(15);
            tester.execute("config count dec 3", sender);

            tester.execute("config names add steve", sender);
            tester.execute("config names remove alex", sender);
            tester.execute("config names clear", sender);

            tester.execute("config scores put \"bob\" 3", sender);
            tester.execute("config scores remove alice", sender);
            tester.execute("config scores clear", sender);
        }

        assertEquals(15, cfg.count.value());
        assertEquals(List.of("alex"), cfg.names.value());
        assertEquals(Map.of("alice", 7), cfg.scores.value());
        SnapshotAssertions.assertMatchesSnapshot("config-command-validation-failure-messages.txt", messages(sender));
    }

    @Test
    void customEntryNameCommandMessagesMatchSnapshot() {
        CustomEntryNameSnapshotConfig cfg = initConfig(new CustomEntryNameSnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config amount 25", sender);
            tester.execute("config amount reset", sender);

            tester.execute("config players add alex", sender);
            tester.execute("config players remove alex", sender);
            tester.execute("config players add alex", sender);
            tester.execute("config players clear", sender);
            tester.execute("config players reset", sender);

            tester.execute("config points put \"alice\" 7", sender);
            tester.execute("config points remove alice", sender);
            tester.execute("config points put \"alice\" 9", sender);
            tester.execute("config points clear", sender);
            tester.execute("config points reset", sender);
        }

        SnapshotAssertions.assertMatchesSnapshot("config-command-custom-entry-name-messages.txt", messages(sender));
    }

    @Test
    void customSuccessMessagesCanBeProvidedByDescriptionProvider() {
        CustomSuccessMessageSnapshotConfig cfg = initConfig(new CustomSuccessMessageSnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).descriptionProvider(
                                                                                           customSuccessMessages())
                                                                                   .build(), "configlib.test")) {
            tester.execute("config count 25", sender);

            tester.execute("config names add alex", sender);
            tester.execute("config names remove alex", sender);
            tester.execute("config names add steve", sender);
            tester.execute("config names clear", sender);

            tester.execute("config scores put \"alice\" 7", sender);
            tester.execute("config scores remove alice", sender);
            tester.execute("config scores put \"bob\" 3", sender);
            tester.execute("config scores clear", sender);
        }

        SnapshotAssertions.assertMatchesSnapshot("config-command-custom-success-messages.txt", messages(sender));
    }

    @Test
    void validationFailuresDoNotDispatchCommandListeners() {
        ListenerValidatedSnapshotConfig cfg = initConfig(new ListenerValidatedSnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config count 13", sender);
            tester.execute("config count inc 3", sender);
            cfg.count.value(15);
            tester.execute("config count dec 3", sender);

            tester.execute("config names add steve", sender);
            tester.execute("config names remove alex", sender);
            tester.execute("config names clear", sender);

            tester.execute("config scores put \"bob\" 3", sender);
            tester.execute("config scores remove alice", sender);
            tester.execute("config scores clear", sender);
        }

        assertEquals(0, cfg.singleModifyCount.get());
        assertEquals(0, cfg.collectionAddCount.get());
        assertEquals(0, cfg.collectionRemoveCount.get());
        assertEquals(0, cfg.collectionClearCount.get());
        assertEquals(0, cfg.mapPutCount.get());
        assertEquals(0, cfg.mapRemoveCount.get());
        assertEquals(0, cfg.mapClearCount.get());
    }

    @Test
    void pojoFieldListMessagesMatchSnapshot() {
        PojoSnapshotConfig cfg = initConfig(new PojoSnapshotConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config list", sender);
            tester.execute("config pojoSnapshotConfig", sender);
        }

        SnapshotAssertions.assertMatchesSnapshot("config-command-pojo-list-messages.txt", messages(sender));
    }

    @Test
    void pojoFieldDiffMessagesMatchSnapshot() {
        PojoSnapshotConfig cfg = initConfig(new PojoSnapshotConfig());
        config = cfg;
        cfg.timer.cancel();
        cfg.maxPlayers = 30;
        cfg.pushHistory();
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(cfg).build(), "configlib.test")) {
            tester.execute("config diff 0 1", sender);
        }

        SnapshotAssertions.assertMatchesSnapshot("config-command-pojo-diff-messages.txt", messages(sender));
    }

    static class SnapshotConfig extends CommonBaseConfig {
        final IntegerValue count = new IntegerValue(10, 0, 100);
        final StringListValue names = new StringListValue().addAllowableString("alex")
                                                           .addAllowableString("steve");
        final String2IntegerMapValue scores = new String2IntegerMapValue(new LinkedHashMap<>()).addAllowableKeyString(
                                                                                                       "alice")
                                                                                               .addAllowableKeyString(
                                                                                                       "bob");
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static class ValidatedSnapshotConfig extends CommonBaseConfig {
        final IntegerValue count = new IntegerValue(10, 0, 100).addValidator(v -> {
            if (v == 13) {
                throw new InvalidValueException("count cannot be 13");
            }
            if (v == 12) {
                throw new InvalidValueException("count cannot be 12");
            }
        });
        final StringListValue names = new StringListValue("alex").addAllowableString("alex")
                                                                 .addAllowableString("steve")
                                                                 .addValidator(v -> {
                                                                     if (v.isEmpty()) {
                                                                         throw new InvalidValueException(
                                                                                 "names cannot be empty");
                                                                     }
                                                                     if (v.contains("steve")) {
                                                                         throw new InvalidValueException(
                                                                                 "names cannot contain steve");
                                                                     }
                                                                 });
        final String2IntegerMapValue scores = new String2IntegerMapValue(new LinkedHashMap<String, Integer>() {{
            put("alice", 7);
        }}).addAllowableKeyString("alice")
           .addAllowableKeyString("bob")
           .addValidator(v -> {
               if (v.isEmpty()) {
                   throw new InvalidValueException("scores cannot be empty");
               }
               if (v.containsKey("bob")) {
                   throw new InvalidValueException("scores cannot contain bob");
               }
           });
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    static class CustomEntryNameSnapshotConfig extends SnapshotConfig {
        CustomEntryNameSnapshotConfig() {
            count.entryName("amount");
            names.entryName("players");
            scores.entryName("points");
        }
    }

    static class CustomSuccessMessageSnapshotConfig extends CommonBaseConfig {
        final IntegerValue count = new IntegerValue(10, 0, 100);
        final StringListValue names = new StringListValue().addAllowableString("alex")
                                                           .addAllowableString("steve");
        final String2IntegerMapValue scores = new String2IntegerMapValue(new LinkedHashMap<>()).addAllowableKeyString(
                                                                                                       "alice")
                                                                                               .addAllowableKeyString(
                                                                                                       "bob");
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }

    private ConfigCommandDescriptions.Provider customSuccessMessages() {
        ConfigCommandDescriptions.Provider fallback = ConfigCommandDescriptions.defaultProvider();
        return (ctx, key, args) -> {
            switch (key) {
                case SINGLE_VALUE_MODIFY_SUCCESS:
                    if ("count".equals(args.get("entry"))) {
                        return "custom " + args.get("entry");
                    }
                    break;
                case COLLECTION_ADD_SUCCESS:
                    return "custom add " + args.get("entry") + "=[" + args.get("value") + "]";
                case COLLECTION_REMOVE_SUCCESS:
                    return "custom remove " + args.get("entry") + "=[" + args.get("value") + "]";
                case COLLECTION_CLEAR_SUCCESS:
                    return "custom clear " + args.get("entry");
                case MAP_PUT_SUCCESS:
                    return "custom put " + args.get("entry") + "=" + args.get("key") + ":" + args.get("value");
                case MAP_REMOVE_SUCCESS:
                    return "custom map remove " + args.get("entry") + "=" + args.get("key") + ":" + args.get("value");
                case MAP_CLEAR_SUCCESS:
                    return "custom map clear " + args.get("entry");
                default:
                    break;
            }
            return fallback.describe(ctx, key, args);
        };
    }

    static class ListenerValidatedSnapshotConfig extends ValidatedSnapshotConfig {
        final transient AtomicInteger singleModifyCount = new AtomicInteger();
        final transient AtomicInteger collectionAddCount = new AtomicInteger();
        final transient AtomicInteger collectionRemoveCount = new AtomicInteger();
        final transient AtomicInteger collectionClearCount = new AtomicInteger();
        final transient AtomicInteger mapPutCount = new AtomicInteger();
        final transient AtomicInteger mapRemoveCount = new AtomicInteger();
        final transient AtomicInteger mapClearCount = new AtomicInteger();

        ListenerValidatedSnapshotConfig() {
            count.onSet(v -> singleModifyCount.incrementAndGet());
            names.onAdd(v -> collectionAddCount.incrementAndGet())
                 .onRemove(v -> collectionRemoveCount.incrementAndGet())
                 .onClear(collectionClearCount::incrementAndGet);
            scores.onPut((k, v) -> mapPutCount.incrementAndGet())
                  .onRemove((k, v) -> mapRemoveCount.incrementAndGet())
                  .onClear(mapClearCount::incrementAndGet);
        }
    }

    static class PojoSnapshotConfig extends CommonBaseConfig {
        @Description("Maximum player count.")
        @Range(min = 1, max = 100)
        public int maxPlayers = 20;
        @Description("Message of the day.")
        public String motd = "hello";
        private String privateValue = "private";
        public transient String runtimeCache = "cache";
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }
}
