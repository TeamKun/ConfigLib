package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.util.ConfigUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigModifyCommand extends Command {
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
            addChildren(new Command(config.entryName()) {{
                init(config, this);
            }});
        }
    }

    private void init(BaseConfig config, Command command) {
        for (Field field : ConfigUtil.getSingleValueFields(config)) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            SingleValue<?, ?> v;
            try {
                v = ((SingleValue<?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.writableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                addChildren(new ModifySetCommand(field, v, config));

                if (v instanceof NumericValue) {
                    addChildren(new ModifyIncCommand(field, ((NumericValue<?, ?>) v), config));
                    addChildren(new ModifyDecCommand(field, ((NumericValue<?, ?>) v), config));
                }
            }});
        }

        for (Field field : ConfigUtil.getCollectionValueFields(config)) {
            CollectionValue<?, ?, ?> v;
            try {
                v = ((CollectionValue<?, ?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.addableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                if (v.addableByCommand()) {
                    addChildren(new ModifyAddCommand(field, v, config));
                }
                if (v.removableByCommand()) {
                    addChildren(new ModifyRemoveCommand(field, v, config));
                }
                if (v.clearableByCommand()) {
                    addChildren(new ModifyClearCommand(field, v, config));
                }
            }});
        }

        for (Field field : ConfigUtil.getMapValueFields(config)) {
            MapValue<?, ?, ?> v;
            try {
                v = ((MapValue<?, ?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.puttableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                if (v.puttableByCommand()) {
                    addChildren(new ModifyMapPutCommand(field, v, config));
                }
                if (v.removableByCommand()) {
                    addChildren(new ModifyMapRemoveCommand(field, v, config));
                }
                if (v.clearableByCommand()) {
                    addChildren(new ModifyMapClearCommand(field, v, config));
                }
            }});
        }
    }
}
