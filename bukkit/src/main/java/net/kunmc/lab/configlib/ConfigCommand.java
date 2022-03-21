package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.AccessibleCommand;
import org.jetbrains.annotations.NotNull;

public class ConfigCommand extends AccessibleCommand {
    ConfigCommand(@NotNull String name) {
        super(name);
    }
}
