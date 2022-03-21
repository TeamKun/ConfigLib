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
            SingleValue<?, ?> v;
            try {
                v = ((SingleValue<?, ?>) field.get(config));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (!v.writableByCommand()) {
                continue;
            }

            command.appendChild(new Command(field.getName()) {
                {
                    children(new ModifySetCommand(field, v, config));

                    if (v instanceof NumericValue) {
                        children(new ModifyIncCommand(field, ((NumericValue<?, ?>) v), config));
                        children(new ModifyDecCommand(field, ((NumericValue<?, ?>) v), config));
                    }
                }
            });
        }

        for (Field field : ConfigUtil.getCollectionValueFields(config)) {
            CollectionValue<?, ?, ?> v;
            try {
                v = ((CollectionValue<?, ?, ?>) field.get(config));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (!v.addableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.appendChild(new Command(field.getName()) {
                {
                    if (v.addableByCommand()) {
                        children(new ModifyAddCommand(field, v, config));
                    }
                    if (v.removableByCommand()) {
                        children(new ModifyRemoveCommand(field, v, config));
                    }
                    if (v.clearableByCommand()) {
                        children(new ModifyClearCommand(field, v, config));
                    }
                }
            });
        }

        for (Field field : ConfigUtil.getMapValueFields(config)) {
            MapValue<?, ?, ?> v;
            try {
                v = ((MapValue<?, ?, ?>) field.get(config));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (!v.puttableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.appendChild(new Command(field.getName()) {
                {
                    if (v.puttableByCommand()) {
                        children(new ModifyMapPutCommand(field, v, config));
                    }
                    if (v.removableByCommand()) {
                        children(new ModifyMapRemoveCommand(field, v, config));
                    }
                    if (v.clearableByCommand()) {
                        children(new ModifyMapClearCommand(field, v, config));
                    }
                }
            });
        }
    }
}
