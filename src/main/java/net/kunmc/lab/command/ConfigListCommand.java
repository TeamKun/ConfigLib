package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.Value;

import java.lang.reflect.Field;

class ConfigListCommand extends Command {
    private final BaseConfig config;

    public ConfigListCommand(BaseConfig config) {
        super("list");
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        for (Field field : config.getClass().getDeclaredFields()) {
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
