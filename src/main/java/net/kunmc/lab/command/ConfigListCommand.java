package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.Value;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

class ConfigListCommand extends Command {
    private BaseConfig config;

    public ConfigListCommand(BaseConfig config) {
        super(SubCommandType.List.name);
        this.config = config;
    }

    public ConfigListCommand(List<BaseConfig> configList) {
        super(SubCommandType.List.name);

        if (configList.isEmpty()) {
            throw new IllegalArgumentException("configList is emptry");
        }

        for (BaseConfig config : configList) {
            children(new Command(config.entryName()) {
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

            Object value = null;
            try {
                value = field.get(config);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (value instanceof Value) {
                Value<?> v = ((Value<?>) value);
                if (v.listable()) {
                    v.sendListMessage(ctx, field.getName());
                }
            } else {
                ctx.success(field.getName() + ": " + value);
            }
        }
    }
}
