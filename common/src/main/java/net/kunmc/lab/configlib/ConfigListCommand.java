package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

class ConfigListCommand extends Command {
    public ConfigListCommand(Set<CommonBaseConfig> configs) {
        super(SubCommandType.List.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() == 1) {
            configs.forEach(config -> {
                execute(ctx -> exec(ctx, config));
            });
        } else {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    execute(ctx -> exec(ctx, config));
                }});
            });
        }
    }

    private void exec(CommandContext ctx, CommonBaseConfig config) {
        for (Field field : config.getClass()
                                 .getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
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
                    ctx.sendMessageWithOption(field.getName() + ": " + v.asString(ctx),
                                              option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                              .hoverText(StringUtils.defaultString(v.description())));
                }
            } else {
                ctx.sendSuccess(field.getName() + ": " + o);
            }
        }
    }
}
