package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.config.BaseConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigCommandBuilder {
    private final Set<BaseConfig> configSet = new HashSet<>();
    private final Map<SubCommandType, Boolean> subCommandTypeEnabledMap = new HashMap<>();

    public ConfigCommandBuilder(@NotNull BaseConfig config) {
        configSet.add(config);

        for (SubCommandType subCommand : SubCommandType.values()) {
            subCommandTypeEnabledMap.put(subCommand, true);
        }
    }

    public ConfigCommandBuilder disableListCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.List, false);
        return this;
    }

    public ConfigCommandBuilder disableAddCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Add, false);
        return this;
    }

    public ConfigCommandBuilder disableRemoveCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Remove, false);
        return this;
    }

    public ConfigCommandBuilder disableClearCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Clear, false);
        return this;
    }

    public ConfigCommandBuilder disableSetCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Set, false);
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Reload, false);
        return this;
    }

    public ConfigCommandBuilder addConfig(@NotNull BaseConfig config) {
        configSet.add(config);
        return this;
    }

    public ConfigCommand build() {
        ConfigCommand configCommand = new ConfigCommand();
        for (Command cmd : createSubCommands()) {
            configCommand.appendChild(cmd);
        }

        return configCommand;
    }

    private Set<Command> createSubCommands() {
        Set<Command> subCommandSet = new HashSet<>();
        for (Map.Entry<SubCommandType, Boolean> entry : subCommandTypeEnabledMap.entrySet()) {
            SubCommandType type = entry.getKey();
            Set<BaseConfig> usedConfigs = type.hasEntryFor(configSet).entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            if (entry.getValue()) {
                Command cmd = createSubCommand(usedConfigs, type);
                if (cmd != null) {
                    subCommandSet.add(cmd);
                }
            }
        }

        return subCommandSet;
    }

    private @Nullable Command createSubCommand(Set<BaseConfig> configSet, SubCommandType type) {
        if (configSet.size() == 0) {
            return null;
        }

        if (configSet.size() == 1) {
            BaseConfig config = configSet.toArray(new BaseConfig[0])[0];
            return type.of(config);
        } else {
            return type.of(configSet);
        }
    }
}