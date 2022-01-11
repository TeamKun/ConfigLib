package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.value.CollectionValue;

import java.lang.reflect.Field;
import java.util.Set;

class ConfigModifyCommand extends AccessibleCommand {
    public ConfigModifyCommand(BaseConfig config) {
        super(SubCommandType.Modify.name);

        init(config, this);
    }

    public ConfigModifyCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Modify.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (BaseConfig config : configSet) {
            children(new AccessibleCommand(config.setEntryName()) {
                {
                    init(config, this);
                }
            });
        }
    }

    private void init(BaseConfig config, AccessibleCommand command) {
        ModifySetCommand.register(config, command);

        for (Field field : ConfigUtil.getCollectionValueFields(config)) {
            command.appendChild(new Command(field.getName()) {
                {
                    try {
                        CollectionValue value = ((CollectionValue) field.get(config));

                        if (value.addableByCommand()) {
                            children(new ModifyAddCommand(field, value, config));
                        }
                        if (value.removableByCommand()) {
                            children(new ModifyRemoveCommand(field, value, config));
                        }
                        if (value.clearableByCommand()) {
                            children(new ModifyClearCommand(field, value, config));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}