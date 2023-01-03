package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommandBuilder {
    private final Set<CommonBaseConfig> configs = new HashSet<>();
    private final Map<SubCommandType, Boolean> subCommandTypeEnabledMap = new HashMap<>();
    private String name = "config";

    public ConfigCommandBuilder(@NotNull CommonBaseConfig config) {
        configs.add(config);

        for (SubCommandType subCommand : SubCommandType.values()) {
            subCommandTypeEnabledMap.put(subCommand, true);
        }
    }

    public ConfigCommandBuilder disableListCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.List, false);
        return this;
    }

    public ConfigCommandBuilder disableModifyCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Modify, false);
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Reload, false);
        return this;
    }

    public ConfigCommandBuilder disableGetCommand() {
        subCommandTypeEnabledMap.put(SubCommandType.Get, false);
        return this;
    }

    public ConfigCommandBuilder addConfig(@NotNull CommonBaseConfig config) {
        configs.add(config);
        return this;
    }

    public ConfigCommandBuilder name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public ConfigCommand build() {
        ConfigCommand configCommand = new ConfigCommand(name);
        createSubCommands().forEach(configCommand::addChildren);
        return configCommand;
    }

    private Set<Command> createSubCommands() {
        Set<Command> subCommands = new HashSet<>();

        for (Map.Entry<SubCommandType, Boolean> entry : subCommandTypeEnabledMap.entrySet()) {
            SubCommandType type = entry.getKey();
            Set<CommonBaseConfig> usedConfigs = type.hasEntryFor(configs)
                                                    .entrySet()
                                                    .stream()
                                                    .peek(e -> e.setValue(e.getValue() && type.isEnabledFor(e.getKey())))
                                                    .filter(Map.Entry::getValue)
                                                    .map(Map.Entry::getKey)
                                                    .collect(Collectors.toSet());

            if (entry.getValue()) {
                createSubCommand(usedConfigs, type).ifPresent(subCommands::add);
            }
        }

        return subCommands;
    }

    private Optional<Command> createSubCommand(Set<CommonBaseConfig> configs, SubCommandType type) {
        if (configs.size() == 0) {
            return Optional.empty();
        }

        if (configs.size() == 1) {
            CommonBaseConfig config = configs.toArray(new CommonBaseConfig[0])[0];
            return Optional.of(type.of(config));
        } else {
            return Optional.of(type.of(configs));
        }
    }
}
