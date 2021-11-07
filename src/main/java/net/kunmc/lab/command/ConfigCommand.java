package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;

public class ConfigCommand extends Command {
    public ConfigCommand(BaseConfig config) {
        super("config");
        children(new ConfigSetCommand(config), new ConfigListCommand(config), new ConfigReloadCommand(config));
    }
}
