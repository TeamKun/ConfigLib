package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.CollectionValue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

class ConfigAddCommand extends AccessibleCommand {
    public ConfigAddCommand(BaseConfig config) {
        super(SubCommandType.Add.name);
        init(config, this);
    }

    public ConfigAddCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Add.name);

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
            CollectionValue<? extends Collection<?>, ?> value = null;
            try {
                value = ((CollectionValue<? extends Collection<?>, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (value.addableByCommand()) {
                command.appendChild(new ConfigItem(field, value, config));
            }
        }
    }

    private static class ConfigItem extends CollectionValueConfigItem {
        public ConfigItem(Field field, CollectionValue<? extends Collection<?>, ?> configValue, BaseConfig config) {
            super(field, configValue, config);
        }

        @Override
        void appendArgument(UsageBuilder builder) {
            configValue.appendArgumentForAdd(builder);
        }

        @Override
        boolean isCorrectArgument(Object argument) {
            return configValue.isCorrectArgumentForAdd(argument);
        }

        @Override
        String incorrectArgumentMessage(Object argument) {
            return configValue.incorrectArgumentMessageForAdd(argument);
        }

        @Override
        Collection argumentToValue(Object argument) {
            return configValue.argumentToValueForAdd(argument);
        }


        @Override
        boolean validate(Collection value) {
            return configValue.validateForAdd(value);
        }

        @Override
        String invalidMessage(String entryName, Collection value) {
            return configValue.invalidValueMessageForAdd(entryName, value);
        }

        @Override
        void writeProcess(CommandContext ctx, String entryName, Collection value) {
            ((Collection) configValue.value()).addAll(value);
            ctx.success(configValue.succeedMessageForAdd(entryName, value));
        }
    }
}
