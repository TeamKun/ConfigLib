package net.kunmc.lab.command;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.CollectionValue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ConfigRemoveCommand extends AccessibleCommand {
    public ConfigRemoveCommand(BaseConfig config) {
        super(SubCommand.Remove.name);
        init(config, this);
    }

    public ConfigRemoveCommand(List<BaseConfig> configList) {
        super(SubCommand.Remove.name);

        if (configList.isEmpty()) {
            throw new IllegalArgumentException("configList is empty");
        }

        for (BaseConfig config : configList) {
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
        boolean validate(Collection value) {
            return configValue.validateOnRemove(value);
        }

        @Override
        String invalidMessage(String entryName, Collection value) {
            return configValue.invalidValueMessageOnRemove(entryName, value);
        }

        @Override
        void writeProcess(CommandContext ctx, String entryName, Collection value) {
            ((Collection) configValue.value()).removeAll(value);
            ctx.success(configValue.succeedMessageOnRemove(entryName, value));
        }
    }
}
