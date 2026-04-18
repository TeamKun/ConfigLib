package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class ConfigLevelCommandTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void listReloadAndResetAreGeneratedForSingleConfig() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config list", sender);
            assertTrue(messages(sender).stream()
                                       .anyMatch(x -> x.contains("count: 10")), messages(sender).toString());

            config.store.writeRaw("{\"count\":{\"value\":33},\"message\":{\"value\":\"loaded\"},\"_version_\":0}");
            tester.execute("config reload", sender);
            assertEquals(33, config.count.value());
            assertEquals("loaded", config.message.value());

            tester.execute("config reset", sender);
            assertEquals(10, config.count.value());
            assertEquals("hello", config.message.value());
        }
    }

    @Test
    void configNameAndTrailingDotListAliasesAreGeneratedForSingleConfig() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config testConfig", sender);
            tester.execute("config testConfig.", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("message: hello")), messages(sender).toString());
    }

    @Test
    void customRootAndConfigNamesAreUsedWhenGeneratingCommands() {
        CustomNamedConfig cfg = initConfig(new CustomNamedConfig());
        config = cfg;
        FakeSender sender = FakeSender.console();
        Command command = new ConfigCommandBuilder(cfg).name("settings")
                                                       .build();

        try (CommandTester tester = new CommandTester(command, "configlib.test")) {
            tester.execute("settings primary", sender);
            tester.execute("settings primary.", sender);
            tester.execute("settings primary.amount 22", sender);

            assertEquals(22, cfg.count.value());
            assertThrows(RuntimeException.class, () -> tester.execute("config primary", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("settings testConfig", sender));
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("amount:")), messages(sender).toString());
    }

    @Test
    void builderCanDisableConfigLevelCommands() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();
        Command command = new ConfigCommandBuilder(config).disableList()
                                                          .disableReload()
                                                          .disableReset()
                                                          .disableHistory()
                                                          .build();

        try (CommandTester tester = new CommandTester(command, "configlib.test")) {
            assertThrows(RuntimeException.class, () -> tester.execute("config list", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config reload", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config reset", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config history", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config undo", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config diff 1", sender));
        }
    }

    @Test
    void configCanDisableItsOwnConfigLevelCommands() {
        DisabledConfig cfg = new DisabledConfig();
        config = cfg;
        init(cfg);
        FakeSender sender = FakeSender.console();
        Command command = commandFor(cfg);

        try (CommandTester tester = new CommandTester(command, "configlib.test")) {
            assertThrows(RuntimeException.class, () -> tester.execute("config list", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config reload", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config reset", sender));
            assertThrows(RuntimeException.class, () -> tester.execute("config history", sender));
        }
    }

    static class DisabledConfig extends TestConfig {
        DisabledConfig() {
            disableList();
            disableReload();
            disableReset();
            disableHistory();
        }
    }
}
