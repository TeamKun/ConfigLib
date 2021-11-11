package net.kunmc.lab.configlib.command;

import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.SingleValue;

import java.lang.reflect.Field;
import java.util.Set;

class ConfigSetCommand extends AccessibleCommand {
    public ConfigSetCommand(BaseConfig config) {
        super(SubCommandType.Set.name);
        init(config, this);
    }

    public ConfigSetCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Set.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is emptry");
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
        try {
            for (Field field : config.getSingleValueFields()) {
                SingleValue<?> v = ((SingleValue<?>) field.get(config));
                if (v.writableByCommand()) {
                    command.appendChild(new SingleValueConfigItem(field, v, config));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
