package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigGetCommand extends Command {
    public ConfigGetCommand(BaseConfig config) {
        super(SubCommandType.Get.name);

        init(config, this);
    }

    public ConfigGetCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.Get.name);

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

                command.addChildren(new Command(field.getName()) {
                    @Override
                    public void execute(@NotNull CommandContext ctx) {
                        v.sendListMessage(ctx, field.getName());
                    }
                });
            } else {
                command.addChildren(new Command(field.getName()) {
                    @Override
                    public void execute(@NotNull CommandContext ctx) {
                        ctx.sendSuccess(field.getName() + ": " + o);
                    }
                });
            }
        }
    }
}
