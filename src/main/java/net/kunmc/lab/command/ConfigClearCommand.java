package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.CollectionValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

class ConfigClearCommand extends AccessibleCommand {
    public ConfigClearCommand(BaseConfig config) {
        super("clear");
        init(config, this);
    }

    public ConfigClearCommand(List<BaseConfig> configList) {
        super("clear");

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

            CollectionValue value = null;
            try {
                value = (CollectionValue) field.get(config);
            } catch (IllegalAccessException exception) {
                exception.printStackTrace();
            }

            if (value.clearableByCommand()) {
                command.appendChild(new ConfigItem(field, value, config));
            }
        }
    }

    private static class ConfigItem extends Command {
        private final Field field;
        private final CollectionValue configValue;
        private final BaseConfig config;

        public ConfigItem(Field field, CollectionValue configValue, BaseConfig config) {
            super(field.getName());
            this.field = field;
            this.configValue = configValue;
            this.config = config;
        }

        @Override
        public void execute(CommandContext ctx) {
            ((Collection) configValue.value()).clear();
            ctx.success(configValue.clearMessage(field.getName()));

            config.saveConfigIfPresent();
        }
    }
}
