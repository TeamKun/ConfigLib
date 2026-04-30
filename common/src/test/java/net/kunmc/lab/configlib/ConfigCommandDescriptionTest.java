package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.UUID;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigCommandDescriptionTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void rootHelpIncludesSubcommandDescriptions() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.player("Steve", "en_us");

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Inspect and modify registered configs.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Browse the change audit log.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Reset config values to their defaults.")),
                   messages(sender).toString());
    }

    @Test
    void historyDiffHelpIncludesArgumentDescriptions() {
        config = init(new TestConfig());
        makeHistory(config);
        FakeSender sender = FakeSender.player("Steve", "en_us");

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config history diff", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Compare history snapshots.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains(
                                           "Compare the current snapshot against the given history index.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains(
                                           "Compare two history indexes. 0 is current, larger values are older.")),
                   messages(sender).toString());
    }

    @Test
    void fieldHelpIncludesModifyDescriptions() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.player("Steve", "en_us");

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config count set", sender);
            tester.execute("config names add", sender);
            tester.execute("config scores put", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Set 'count' to a new value.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Add element(s) to 'names'.")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("Put or replace a key-value pair in 'scores'.")),
                   messages(sender).toString());
    }

    @Test
    void descriptionsUseSenderLanguage() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.player("Steve", "ja_jp");
        Mockito.when(((Player) sender.asSender()).getUniqueId())
               .thenReturn(UUID.randomUUID());

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config", sender);
            tester.execute("config count 25", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("設定を表示・変更します。")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("変更監査ログを表示します。")),
                   messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count の値を 25 に変更しました。")),
                   messages(sender).toString());
    }

    @Test
    void descriptionProviderCanBeCustomized() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(config).descriptionProvider((ctx, key, args) -> "custom:" + key)
                                                                                      .build(), "configlib.test")) {
            tester.execute("config", sender);
            tester.execute("config count 25", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("custom:ROOT")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("custom:AUDIT")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("custom:SINGLE_VALUE_MODIFY_SUCCESS")),
                   messages(sender).toString());
    }

    @Test
    void namedArgumentsCanBeReorderedByTranslation() {
        config = init(new TestConfig());
        ConfigCommandDescriptions.Provider provider = ConfigCommandDescriptions.localized(Map.of("en_us",
                                                                                                 Map.of(ConfigCommandDescriptions.Key.POJO_RANGE,
                                                                                                        "{max} >= {field} >= {min}")));
        FakeSender sender = FakeSender.player("Steve", "en_us");

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(config).descriptionProvider((ctx, key, args) -> {
                                                                                          if (key == ConfigCommandDescriptions.Key.ROOT) {
                                                                                              return provider.describe(ctx,
                                                                                                                       ConfigCommandDescriptions.Key.POJO_RANGE,
                                                                                                                       ConfigCommandDescriptions.Args.of("field", "count", "min", 0, "max", 100));
                                                                                          }
                                                                                          return ConfigCommandDescriptions.defaultProvider()
                                                                                                                          .describe(ctx, key, args);
                                                                                      })
                                                                                      .build(), "configlib.test")) {
            tester.execute("config", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("100 >= count >= 0")), messages(sender).toString());
    }

    @Test
    void namedArgumentsAllowNullValues() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.player("Steve", "en_us");

        try (CommandTester tester = new CommandTester(new ConfigCommandBuilder(config).descriptionProvider((ctx, key, args) -> {
                                                                                          if (key == ConfigCommandDescriptions.Key.ROOT) {
                                                                                              return ConfigCommandDescriptions.defaultProvider()
                                                                                                                              .describe(ctx,
                                                                                                                                        ConfigCommandDescriptions.Key.FIELD_RESET_SUCCESS,
                                                                                                                                        ConfigCommandDescriptions.Args.of("entry",
                                                                                                                                                                          "message",
                                                                                                                                                                          "value",
                                                                                                                                                                          null));
                                                                                          }
                                                                                          return ConfigCommandDescriptions.defaultProvider()
                                                                                                                          .describe(ctx, key, args);
                                                                                      })
                                                                                      .build(), "configlib.test")) {
            tester.execute("config", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("message was reset to default (null).")),
                   messages(sender).toString());
    }

    @Test
    void defaultValueCommandPrerequisiteMessageUsesSenderLanguage() {
        ExecutableIfConfig cfg = initConfig(new ExecutableIfConfig());
        config = cfg;
        FakeSender sender = FakeSender.player("Steve", "ja_jp");

        try (CommandTester tester = new CommandTester(commandFor(cfg), "configlib.test")) {
            tester.execute("config count 25", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("このコマンドを実行できません。")),
                   messages(sender).toString());
    }

    static class ExecutableIfConfig extends TestConfig {
        ExecutableIfConfig() {
            count.executableIf(ctx -> false);
        }
    }
}
