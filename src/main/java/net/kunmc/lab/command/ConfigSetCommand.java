package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class ConfigSetCommand extends Command {
    public ConfigSetCommand(BaseConfig config) {
        super("set");
        try {
            for (Field field : config.getClass().getDeclaredFields()) {
                if (!Modifier.isPublic(field.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);

                Object value = field.get(config);
                if (value instanceof Value<?>) {
                    Value<?> v = ((Value<?>) value);
                    if (v.writableByCommand()) {
                        children(new ValueConfigItem(field, v, config));
                    }
                } else {
                    children(new PrimitiveConfigItem(field, config));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
