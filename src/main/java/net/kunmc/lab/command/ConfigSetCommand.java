package net.kunmc.lab.command;

import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.SingleValue;

import java.lang.reflect.Field;
import java.util.List;

class ConfigSetCommand extends AccessibleCommand {
    public ConfigSetCommand(BaseConfig config) {
        super(SubCommand.Set.name);
        init(config, this);
    }

    public ConfigSetCommand(List<BaseConfig> configList) {
        super(SubCommand.Set.name);

        if (configList.isEmpty()) {
            throw new IllegalArgumentException("configList is emptry");
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
