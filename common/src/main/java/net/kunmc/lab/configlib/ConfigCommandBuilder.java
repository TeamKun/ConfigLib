package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommandBuilder {
    private final List<CommonBaseConfig> configs = new ArrayList<>();
    private String name = "config";
    private boolean listEnabled = true;
    private boolean reloadEnabled = true;
    private boolean resetEnabled = true;
    private boolean historyEnabled = true;
    private boolean getEnabled = true;
    private boolean modifyEnabled = true;

    public ConfigCommandBuilder(@NotNull CommonBaseConfig config) {
        configs.add(config);
    }

    public ConfigCommandBuilder disableList() {
        listEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableReload() {
        reloadEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableReset() {
        resetEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableHistory() {
        historyEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableGet() {
        getEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableModify() {
        modifyEnabled = false;
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

    public ConfigCommandBuilder sort() {
        return sort(Comparator.comparing(CommonBaseConfig::entryName));
    }

    public ConfigCommandBuilder sort(Comparator<? super CommonBaseConfig> sorter) {
        configs.sort(sorter);
        return this;
    }

    public ConfigCommand build() {
        ConfigCommand configCommand = new ConfigCommand(name);

        if (listEnabled) {
            createSubCommand(SubCommandType.List).ifPresent(configCommand::addChildren);
        }
        if (reloadEnabled) {
            createSubCommand(SubCommandType.Reload).ifPresent(configCommand::addChildren);
        }
        if (resetEnabled) {
            createSubCommand(SubCommandType.Reset).ifPresent(configCommand::addChildren);
        }
        if (historyEnabled) {
            createSubCommand(SubCommandType.History).ifPresent(configCommand::addChildren);
            createSubCommand(SubCommandType.Audit).ifPresent(configCommand::addChildren);
            createSubCommand(SubCommandType.Undo).ifPresent(configCommand::addChildren);
            createSubCommand(SubCommandType.Diff).ifPresent(configCommand::addChildren);
        }

        Set<String> conflictingFieldNames = detectConflictingFieldNames();
        configs.forEach(config -> addFieldCommandsFor(configCommand, config, conflictingFieldNames));

        return configCommand;
    }

    private Optional<Command> createSubCommand(SubCommandType type) {
        Set<CommonBaseConfig> applicable = configs.stream()
                                                  .filter(type::isEnabledFor)
                                                  .filter(type::hasEntryFor)
                                                  .collect(Collectors.toCollection(LinkedHashSet::new));
        if (applicable.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(type.of(applicable));
    }

    /**
     * Returns field names that appear in more than one config and would generate a command.
     */
    private Set<String> detectConflictingFieldNames() {
        Map<String, Integer> nameCount = new HashMap<>();
        for (CommonBaseConfig config : configs) {
            for (ConfigSchemaEntry<?> entry : getCommandEntries(config)) {
                nameCount.merge(entry.entryName(), 1, Integer::sum);
            }
        }
        return nameCount.entrySet()
                        .stream()
                        .filter(e -> e.getValue() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
    }


    private List<ConfigSchemaEntry<?>> getCommandEntries(CommonBaseConfig config) {
        return config.schema()
                     .entries()
                     .stream()
                     .filter(e -> getEnabled || (modifyEnabled && e.supportsModificationCommand()))
                     .collect(Collectors.toList());
    }

    private void addFieldCommandsFor(ConfigCommand configCommand,
                                     CommonBaseConfig config,
                                     Set<String> conflictingFieldNames) {
        configCommand.addChildren(new Command(config.entryName()) {{
            execute(ctx -> ConfigListCommand.listFields(ctx, config));
            if (configs.size() > 1) {
                Set<CommonBaseConfig> singleton = Collections.singleton(config);
                if (historyEnabled && config.isHistoryEnabled()) {
                    addChildren(new ConfigHistoryCommand(singleton));
                    addChildren(new ConfigAuditCommand(singleton));
                    addChildren(new ConfigUndoCommand(singleton));
                    addChildren(new ConfigDiffCommand(singleton));
                }
            }
        }});
        configCommand.addChildren(new Command(config.entryName() + ".") {{
            execute(ctx -> ConfigListCommand.listFields(ctx, config));
        }});

        for (ConfigSchemaEntry<?> entry : getCommandEntries(config)) {
            String valueEntryName = entry.entryName();
            String prefixedName = config.entryName() + "." + valueEntryName;

            // Prefixed command is always available
            configCommand.addChildren(new ConfigFieldCommand(config, prefixedName, entry, getEnabled, modifyEnabled));

            // Non-prefixed command only when no conflict
            if (!conflictingFieldNames.contains(valueEntryName)) {
                configCommand.addChildren(new ConfigFieldCommand(config,
                                                                 valueEntryName,
                                                                 entry,
                                                                 getEnabled,
                                                                 modifyEnabled));
            }
        }
    }
}
