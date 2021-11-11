package net.kunmc.lab.command;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.CollectionValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

public class ConfigAddCommand extends AccessibleCommand {
    public ConfigAddCommand(BaseConfig config) {
        super("add");
        init(config, this);
    }

    public ConfigAddCommand(List<BaseConfig> configList) {
        super("add");

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
        for (Field field : config.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!CollectionValue.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);

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
        boolean validate(Collection value) {
            return configValue.validateOnAdd(value);
        }

        @Override
        String invalidMessage(String entryName, Collection value) {
            return configValue.invalidValueMessageOnAdd(entryName, value);
        }

        @Override
        void writeProcess(CommandContext ctx, String entryName, Collection value) {
            ((Collection) configValue.value()).addAll(value);
            ctx.success(configValue.succeedMessageOnAdd(entryName, value));
        }
    }
}
