package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConfigCommandBuilder {
    private final Set<BaseConfig> configSet = new HashSet<>();
    private final Map<SubCommandType, Boolean> subCommandTypeBooleanMap = new HashMap<>();

    public ConfigCommandBuilder(@NotNull BaseConfig config) {
        configSet.add(config);

        for (SubCommandType subCommand : SubCommandType.values()) {
            subCommandTypeBooleanMap.put(subCommand, true);
        }
    }

    public ConfigCommandBuilder disableListCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.List, false);
        return this;
    }

    public ConfigCommandBuilder disableAddCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.Add, false);
        return this;
    }

    public ConfigCommandBuilder disableRemoveCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.Remove, false);
        return this;
    }

    public ConfigCommandBuilder disableClearCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.Clear, false);
        return this;
    }

    public ConfigCommandBuilder disableSetCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.Set, false);
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        subCommandTypeBooleanMap.put(SubCommandType.Reload, false);
        return this;
    }

    public ConfigCommandBuilder addConfig(@NotNull BaseConfig config) {
        configSet.add(config);
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

        for (Map.Entry<SubCommandType, Boolean> entry : subCommandTypeBooleanMap.entrySet()) {
            SubCommandType type = entry.getKey();
            boolean b = entry.getValue();
            subCommandTypeBooleanMap.put(type, type.hasEntryFor(configSet) && b);
        }

        if (configSet.size() == 1) {
            BaseConfig config = configSet.toArray(new BaseConfig[0])[0];

            for (Map.Entry<SubCommandType, Boolean> entry : subCommandTypeBooleanMap.entrySet()) {
                if (entry.getValue()) {
                    subCommandList.add(entry.getKey().of(config));
                }
            }
        } else {
            for (Map.Entry<SubCommandType, Boolean> entry : subCommandTypeBooleanMap.entrySet()) {
                if (entry.getValue()) {
                    subCommandList.add(entry.getKey().of(configSet));
                }
            }
        }

        return subCommandList;
    }
}
