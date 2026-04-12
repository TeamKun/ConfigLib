package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigCollectionMapCommandTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void collectionAddRemoveClearAndResetAreGenerated() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config names add alex", sender);
            tester.execute("config names add steve", sender);
            assertEquals(List.of("alex", "steve"), config.names.value());

            tester.execute("config names remove alex", sender);
            assertEquals(List.of("steve"), config.names.value());

            tester.execute("config names clear", sender);
            assertTrue(config.names.value()
                                   .isEmpty());

            tester.execute("config names add alex", sender);
            tester.execute("config names reset", sender);
            assertTrue(config.names.value()
                                   .isEmpty());
        }
    }

    @Test
    void mapPutRemoveClearAndResetAreGenerated() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config scores put \"alice\" 7", sender);
            tester.execute("config scores put \"bob\" 3", sender);
            assertEquals(7, config.scores.get("alice"));
            assertEquals(3, config.scores.get("bob"));

            tester.execute("config scores remove alice", sender);
            assertFalse(config.scores.containsKey("alice"));
            assertTrue(config.scores.containsKey("bob"));

            tester.execute("config scores clear", sender);
            assertTrue(config.scores.isEmpty());

            tester.execute("config scores put \"alice\" 9", sender);
            tester.execute("config scores reset", sender);
            assertTrue(config.scores.isEmpty());
        }
    }

    @Test
    void collectionAndMapGetShowDisplayStrings() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();
        config.names.add("alex");
        config.scores.put("alice", 7);

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config names", sender);
            tester.execute("config scores", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("names: [alex]")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("scores: {alice:7}")), messages(sender).toString());
    }

    @Test
    void disabledCollectionOperationsAreNotGenerated() {
        config = init(new TestConfig());
        config.names.disableAdd()
                    .disableRemove()
                    .disableClear();
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            assertThrows(RuntimeException.class, () -> tester.execute("config names add alex", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config names remove alex", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config names clear", sender));

            tester.execute("config names reset", sender);
        }
    }

    @Test
    void disabledMapOperationsAreNotGenerated() {
        config = init(new TestConfig());
        config.scores.disablePut()
                     .disableRemove()
                     .disableClear();
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            assertThrows(RuntimeException.class, () -> tester.execute("config scores put \"alice\" 7", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config scores remove alice", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config scores clear", sender));

            tester.execute("config scores reset", sender);
        }
    }
}
