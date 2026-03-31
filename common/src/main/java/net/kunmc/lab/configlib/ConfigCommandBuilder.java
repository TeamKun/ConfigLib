package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommandBuilder {
    private final List<CommonBaseConfig> configs = new ArrayList<>();
    private String name = "config";
    private boolean listEnabled = true;
    private boolean reloadEnabled = true;
    private boolean getEnabled = true;
    private boolean modifyEnabled = true;

    public ConfigCommandBuilder(@NotNull CommonBaseConfig config) {
        configs.add(config);
    }

    public ConfigCommandBuilder disableListCommand() {
        listEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableReloadCommand() {
        reloadEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableGetCommand() {
        getEnabled = false;
        return this;
    }

    public ConfigCommandBuilder disableModifyCommand() {
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
            for (Field field : getCommandFields(config)) {
                try {
                    String name = resolveEntryName(field, field.get(config));
                    nameCount.merge(name, 1, Integer::sum);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return nameCount.entrySet()
                        .stream()
                        .filter(e -> e.getValue() > 1)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
    }

    private static String resolveEntryName(Field field, Object obj) {
        if (obj instanceof Value) {
            return ((Value<?, ?>) obj).resolveEntryName(field.getName());
        }
        return field.getName();
    }

    private List<Field> getCommandFields(CommonBaseConfig config) {
        List<Field> result = new ArrayList<>();
        ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())
                      .stream()
                      .filter(f -> !Modifier.isStatic(f.getModifiers()))
                      .filter(f -> !Modifier.isTransient(f.getModifiers()))
                      .forEach(field -> {
                          field.setAccessible(true);
                          Object obj;
                          try {
                              obj = field.get(config);
                          } catch (IllegalAccessException e) {
                              throw new RuntimeException(e);
                          }
                          if ((getEnabled && isDisplayable(obj)) || (modifyEnabled && isModifiable(obj))) {
                              result.add(field);
                          }
                      });
        return result;
    }

    private void addFieldCommandsFor(ConfigCommand configCommand,
                                     CommonBaseConfig config,
                                     Set<String> conflictingFieldNames) {
        configCommand.addChildren(new Command(config.entryName()) {{
            execute(ctx -> ConfigListCommand.listFields(ctx, config));
        }});
        configCommand.addChildren(new Command(config.entryName() + ".") {{
            execute(ctx -> ConfigListCommand.listFields(ctx, config));
        }});

        for (Field field : getCommandFields(config)) {
            Object obj;
            try {
                obj = field.get(config);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            String valueEntryName = resolveEntryName(field, obj);
            String prefixedName = config.entryName() + "." + valueEntryName;

            // Prefixed command is always available
            configCommand.addChildren(new ConfigFieldCommand(prefixedName, field, obj, getEnabled, modifyEnabled));

            // Non-prefixed command only when no conflict
            if (!conflictingFieldNames.contains(valueEntryName)) {
                configCommand.addChildren(new ConfigFieldCommand(valueEntryName,
                                                                 field,
                                                                 obj,
                                                                 getEnabled,
                                                                 modifyEnabled));
            }
        }
    }

    private static boolean isDisplayable(Object obj) {
        if (obj instanceof Value) {
            return ((Value<?, ?>) obj).listable();
        }
        return true;
    }

    private static boolean isModifiable(Object obj) {
        if (obj instanceof SingleValue) {
            return ((SingleValue<?, ?>) obj).writableByCommand();
        }
        if (obj instanceof CollectionValue) {
            CollectionValue<?, ?, ?> v = (CollectionValue<?, ?, ?>) obj;
            return v.addableByCommand() || v.removableByCommand() || v.clearableByCommand();
        }
        if (obj instanceof MapValue) {
            MapValue<?, ?, ?> v = (MapValue<?, ?, ?>) obj;
            return v.puttableByCommand() || v.removableByCommand() || v.clearableByCommand();
        }
        return false;
    }
}
