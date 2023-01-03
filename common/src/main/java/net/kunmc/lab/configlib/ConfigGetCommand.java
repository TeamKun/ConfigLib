package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigGetCommand extends Command {
    public ConfigGetCommand(CommonBaseConfig config) {
        super(SubCommandType.Get.name);

        init(config, this);
    }

    public ConfigGetCommand(Set<CommonBaseConfig> configSet) {
        super(SubCommandType.Get.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (CommonBaseConfig config : configSet) {
            addChildren(new Command(config.entryName()) {{
                init(config, this);
            }});
        }
    }

    private void init(CommonBaseConfig config, Command command) {
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
                if (!v.listable()) {
                    continue;
                }

                command.addChildren(new Command(field.getName()) {{
                    execute(ctx -> v.listMessages(ctx, field.getName())
                                    .forEach(ctx::sendSuccess));
                }});
            } else {
                command.addChildren(new Command(field.getName()) {{
                    execute(ctx -> ctx.sendSuccess(field.getName() + ": " + o));
                }});
            }
        }
    }
}
