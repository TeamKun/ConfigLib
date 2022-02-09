package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.command.AccessibleCommand;
import net.kunmc.lab.configlib.util.ConfigUtil;

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
            children(new AccessibleCommand(config.entryName()) {
                {
                    init(config, this);
                }
            });
        }
    }

    private void init(BaseConfig config, AccessibleCommand command) {
        for (Field field : ConfigUtil.getSingleValueFields(config)) {
            command.appendChild(new Command(field.getName()) {
                {
                    try {
                        SingleValue<?, ?> v = ((SingleValue<?, ?>) field.get(config));
                        if (v.writableByCommand()) {
                            children(new ModifySetCommand(field, v, config));

                            if (v instanceof NumericValue) {
                                children(new ModifyIncCommand(field, ((NumericValue<?, ?>) v), config));
                                children(new ModifyDecCommand(field, ((NumericValue<?, ?>) v), config));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

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

        for (Field field : ConfigUtil.getMapValueFields(config)) {
            command.appendChild(new Command(field.getName()) {
                {
                    try {
                        MapValue value = ((MapValue) field.get(config));

                        if (value.puttableByCommand()) {
                            children(new ModifyMapPutCommand(field, value, config));
                        }
                        if (value.removableByCommand()) {
                            children(new ModifyMapRemoveCommand(field, value, config));
                        }
                        if (value.clearableByCommand()) {
                            children(new ModifyMapClearCommand(field, value, config));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
