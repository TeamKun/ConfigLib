package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigListCommand extends Command {
    public ConfigListCommand(BaseConfig config) {
        super(SubCommandType.List.name);

        execute(ctx -> exec(ctx, config));
    }

    public ConfigListCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.List.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (BaseConfig config : configSet) {
            addChildren(new Command(config.entryName()) {{
                execute(ctx -> exec(ctx, config));
            }});
        }
    }

    private void exec(CommandContext ctx, BaseConfig config) {
        for (Field field : config.getClass()
                                 .getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);

            Object o;
            try {
                o = field.get(config);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (o instanceof Value) {
                Value<?, ?> v = ((Value<?, ?>) o);
                if (v.listable()) {
                    v.sendListMessage(ctx, field.getName());
                }
            } else {
                ctx.sendSuccess(field.getName() + ": " + o);
            }
        }
    }
}
