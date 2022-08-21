package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigListCommand extends Command {
    private BaseConfig config;

    public ConfigListCommand(BaseConfig config) {
        super(SubCommandType.List.name);
        this.config = config;
    }

    public ConfigListCommand(Set<BaseConfig> configs) {
        super(SubCommandType.List.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        for (BaseConfig config : configs) {
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
        // configsのコンストラクタで生成された時はconfigフィールドがnullになる
        if (config == null) {
            ctx.sendHelp();
        } else {
            exec(ctx, config);
        }
    }

    private void exec(CommandContext ctx, BaseConfig config) {
        for (Field field : config.getClass().getDeclaredFields()) {
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
