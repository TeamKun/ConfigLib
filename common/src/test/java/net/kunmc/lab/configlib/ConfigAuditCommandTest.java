package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import net.kunmc.lab.commandlib.CommandTester;
import net.kunmc.lab.commandlib.FakeSender;
import net.kunmc.lab.configlib.annotation.Masked;
import net.kunmc.lab.configlib.store.ConfigStore;
import net.kunmc.lab.configlib.store.InMemoryConfigStore;
import net.kunmc.lab.configlib.value.StringValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static net.kunmc.lab.configlib.ConfigCommandTestSupport.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigAuditCommandTest {
    private TestConfig config;

    @AfterEach
    void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void auditCommandShowsCommandSourceAndPath() {
        config = init(new TestConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(config), "configlib.test")) {
            tester.execute("config count 25", sender);
            tester.execute("config audit", sender);
            tester.execute("config audit 0", sender);
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("COMMAND")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count")), messages(sender).toString());
        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("count: 10 -> 25")), messages(sender).toString());
    }

    @Test
    void auditCommandMasksConfiguredValues() {
        MaskedAuditConfig masked = initConfig(new MaskedAuditConfig());
        FakeSender sender = FakeSender.console();

        try (CommandTester tester = new CommandTester(commandFor(masked), "configlib.test")) {
            tester.execute("config secret updated", sender);
            tester.execute("config audit 0", sender);
        } finally {
            masked.close();
        }

        assertTrue(messages(sender).stream()
                                   .anyMatch(x -> x.contains("secret: <masked> -> <masked>")),
                   messages(sender).toString());
    }

    static class MaskedAuditConfig extends CommonBaseConfig {
        @Masked
        final StringValue secret = new StringValue("initial");
        final transient InMemoryConfigStore store = new InMemoryConfigStore(new Gson());

        @Override
        protected ConfigStore createConfigStore() {
            return store;
        }
    }
}
