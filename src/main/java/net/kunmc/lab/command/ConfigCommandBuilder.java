package net.kunmc.lab.command;

import net.kunmc.lab.config.BaseConfig;

public class ConfigCommandBuilder {
    private final BaseConfig config;
    private boolean shouldUseList = true;
    private boolean shouldUseSet = true;
    private boolean shouldUseReload = true;

    public ConfigCommandBuilder(BaseConfig config) {
        this.config = config;
    }

    public ConfigCommandBuilder disableListCommand() {
        this.shouldUseList = false;
        return this;
    }

    public ConfigCommandBuilder disableSetCommand() {
        this.shouldUseSet = false;
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        this.shouldUseReload = false;
        return this;
    }

    public ConfigCommand build() {
        ConfigCommand configCommand = new ConfigCommand();
        if (shouldUseList) {
            configCommand.appendChild(new ConfigListCommand(config));
        }
        if (shouldUseSet) {
            configCommand.appendChild(new ConfigSetCommand(config));
        }
        if (shouldUseReload) {
            configCommand.appendChild(new ConfigReloadCommand(config));
        }

        return configCommand;
    }
}
