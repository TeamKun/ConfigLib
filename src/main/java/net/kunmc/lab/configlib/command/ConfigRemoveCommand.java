package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.CollectionValue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

class ConfigRemoveCommand extends AccessibleCommand {
    public ConfigRemoveCommand(BaseConfig config) {
        super(SubCommandType.Remove.name);
        init(config, this);
    }

    public ConfigRemoveCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Remove.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (BaseConfig config : configSet) {
            children(new AccessibleCommand(config.entryName()) {
                {
                    init(config, this);
                }
            });
        }
    }

    private static void init(BaseConfig config, AccessibleCommand command) {
        for (Field field : config.getCollectionValueFields()) {
            CollectionValue value = null;
            try {
                value = (CollectionValue) field.get(config);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }

            if (value.removableByCommand()) {
                command.appendChild(new ConfigItem(field, value, config));
            }
        }
    }

    private static class ConfigItem extends CollectionValueConfigItem {
        public ConfigItem(Field field, CollectionValue configValue, BaseConfig config) {
            super(field, configValue, config);
        }

        @Override
        void appendArgument(UsageBuilder builder) {
            configValue.appendArgumentForRemove(builder);
        }

        @Override
        boolean isCorrectArgument(Object argument) {
            return configValue.isCorrectArgumentForRemove(argument);
        }

        @Override
        String incorrectArgumentMessage(Object argument) {
            return configValue.incorrectArgumentMessageForRemove(argument);
        }

        @Override
        Collection argumentToValue(Object argument) {
            return configValue.argumentToValueForRemove(argument);
        }

        @Override
        boolean validate(Collection value) {
            return configValue.validateForRemove(value);
        }

        @Override
        String invalidMessage(String entryName, Collection value) {
            return configValue.invalidValueMessageForRemove(entryName, value);
        }

        @Override
        void writeProcess(CommandContext ctx, String entryName, Collection value) {
            ((Collection) configValue.value()).removeAll(value);
            ctx.success(configValue.succeedMessageForRemove(entryName, value));
        }
    }
}
