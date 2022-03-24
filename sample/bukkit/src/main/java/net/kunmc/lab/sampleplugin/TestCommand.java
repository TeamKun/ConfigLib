package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.ConfigCommand;

public class TestCommand extends Command {
    public TestCommand(ConfigCommand configCommand) {
        super("test");

        addChildren(configCommand);
    }
}
