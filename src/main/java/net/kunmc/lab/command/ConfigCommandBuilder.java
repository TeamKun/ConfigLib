package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigCommandBuilder {
    private final List<BaseConfig> configList = new ArrayList<>();
    private boolean shouldUseList = true;
    private boolean shouldUseAdd = true;
    private boolean shouldUseSet = true;
    private boolean shouldUseReload = true;

    public ConfigCommandBuilder(@NotNull BaseConfig config) {
        configList.add(config);
    }

    public ConfigCommandBuilder disableListCommand() {
        this.shouldUseList = false;
        return this;
    }

    public ConfigCommandBuilder disableAddCommand() {
        this.shouldUseAdd = false;
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

    public ConfigCommandBuilder addConfig(@NotNull BaseConfig config) {
        configList.add(config);
        return this;
    }

    public ConfigCommand build() {
        ConfigCommand configCommand = new ConfigCommand();
        for (Command c : createSubCommands()) {
            configCommand.appendChild(c);
        }

        return configCommand;
    }

    private List<Command> createSubCommands() {
        List<Command> subCommandList = new ArrayList<>();

        if (configList.size() == 1) {
            BaseConfig config = configList.get(0);
            if (shouldUseList) {
                subCommandList.add(new ConfigListCommand(config));
            }
            if (shouldUseAdd) {
                subCommandList.add(new ConfigAddCommand(config));
            }
            if (shouldUseSet) {
                subCommandList.add(new ConfigSetCommand(config));
            }
            if (shouldUseReload) {
                subCommandList.add(new ConfigReloadCommand(config));
            }
        } else {
            if (shouldUseList) {
                subCommandList.add(new ConfigListCommand(configList));
            }
            if (shouldUseAdd) {
                subCommandList.add(new ConfigAddCommand(configList));
            }
            if (shouldUseSet) {
                subCommandList.add(new ConfigSetCommand(configList));
            }
            if (shouldUseReload) {
                subCommandList.add(new ConfigReloadCommand(configList));
            }
        }

        return subCommandList;
    }
}
