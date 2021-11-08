package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;

public class ConfigCommand extends Command {
    ConfigCommand() {
        super("config");
    }

    public void appendChild(Command command) {
        children(command);
    }
}
