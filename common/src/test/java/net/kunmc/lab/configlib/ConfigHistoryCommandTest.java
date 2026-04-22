package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigHistoryCommandTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void historyListAndDetailAreGenerated() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config history", sender);
            tester.execute("config history 1", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("[0]:")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count: 15")), messages(sender).toString());
    }

    @Test
    void historyDiffCommandsAreGenerated() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config history diff 1", sender);
            tester.execute("config history diff 0 2", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count:")), messages(sender).toString());
    }

    @Test
    void topLevelDiffCommandsAreGenerated() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config diff 1", sender);
            tester.execute("config diff 0 2", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count:")), messages(sender).toString());
    }

    @Test
    void diffDefaultCommandShowsDifferenceFromInitialDefaults() {
        config = init(new TestConfig());
        config.count.value(25);
        config.message.value("changed");
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config diff default", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count: 10")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("message: hello")), messages(sender).toString());
    }

    @Test
    void diffDefaultIncludesNestedPojoEntries() {
        NestedPojoModifyConfig cfg = initConfig(new NestedPojoModifyConfig());
        config = null;
        cfg.arena.maxArenas = 12;
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(cfg), "configlib.test")) {
            tester.execute("config diff default", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("arena.maxArenas: 5 -> 12")), messages(sender).toString());
        cfg.close();
    }

    @Test
    void historyUndoCommandsAreGenerated() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config history undo", sender);
            assertEquals(15, config.count.value());

            tester.execute("config history undo 1", sender);
            assertEquals(10, config.count.value());
        }
    }

    @Test
    void topLevelUndoCommandsAreGenerated() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config undo", sender);
            assertEquals(15, config.count.value());

            tester.execute("config undo 1", sender);
            assertEquals(10, config.count.value());
        }
    }
}
