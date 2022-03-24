package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Set;

class ConfigListCommand extends Command {
    private BaseConfig config;

    public ConfigListCommand(BaseConfig config) {
        super(SubCommandType.List.name);
        this.config = config;
    }

    public ConfigListCommand(Set<BaseConfig> configSet) {
        super(SubCommandType.List.name);

        if (configSet.isEmpty()) {
            throw new IllegalArgumentException("configSet is empty");
        }

        for (BaseConfig config : configSet) {
            addChildren(new Command(config.entryName()) {
                @Override
                public void execute(@NotNull CommandContext ctx) {
                    exec(ctx, config);
                }
            });
        }
    }

    @Override
    public void execute(CommandContext ctx) {
        if (config == null) {
            ctx.sendHelp();
        } else {
            exec(ctx, config);
        }
    }

    private void exec(CommandContext ctx, BaseConfig config) {
        for (Field field : config.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            Object o;
            try {
                o = field.get(config);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
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
