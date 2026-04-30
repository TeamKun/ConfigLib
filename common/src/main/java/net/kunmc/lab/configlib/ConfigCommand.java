package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.jetbrains.annotations.NotNull;

public class ConfigCommand extends Command {
    ConfigCommand(@NotNull String name, @NotNull ConfigCommandDescriptions.Provider descriptions) {
        super(name);
        description(ConfigCommandDescriptions.root(descriptions));
    }
}
