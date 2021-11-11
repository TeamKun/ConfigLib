package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigCommandBuilder {
    private final List<BaseConfig> configList = new ArrayList<>();
    private final Map<SubCommand, Boolean> subCommandBooleanMap = new HashMap<>();

    public ConfigCommandBuilder(@NotNull BaseConfig config) {
        configList.add(config);

        for (SubCommand subCommand : SubCommand.values()) {
            subCommandBooleanMap.put(subCommand, true);
        }
    }

    public ConfigCommandBuilder disableListCommand() {
        subCommandBooleanMap.put(SubCommand.List, false);
        return this;
    }

    public ConfigCommandBuilder disableAddCommand() {
        subCommandBooleanMap.put(SubCommand.Add, false);
        return this;
    }

    public ConfigCommandBuilder disableRemoveCommand() {
        subCommandBooleanMap.put(SubCommand.Remove, false);
        return this;
    }

    public ConfigCommandBuilder disableClearCommand() {
        subCommandBooleanMap.put(SubCommand.Clear, false);
        return this;
    }

    public ConfigCommandBuilder disableSetCommand() {
        subCommandBooleanMap.put(SubCommand.Set, false);
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        subCommandBooleanMap.put(SubCommand.Reload, false);
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

            for (Map.Entry<SubCommand, Boolean> entry : subCommandBooleanMap.entrySet()) {
                if (entry.getValue()) {
                    subCommandList.add(entry.getKey().of(config));
                }
            }
        } else {
            for (Map.Entry<SubCommand, Boolean> entry : subCommandBooleanMap.entrySet()) {
                if (entry.getValue()) {
                    subCommandList.add(entry.getKey().of(configList));
                }
            }
        }

        return subCommandList;
    }
}
