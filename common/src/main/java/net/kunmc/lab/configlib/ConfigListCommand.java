package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;
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

        execute(ctx -> configs.forEach(config -> {
            listFields(ctx, config);
        }));

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    execute(ctx -> {
                        listFields(ctx, config);
                    });
                }});
            });
        }
    }

    static void listFields(CommandContext ctx, CommonBaseConfig config) {
        ctx.sendMessage(ConfigUtil.configHeader(config));
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
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
                ctx.sendMessageWithOption(v.resolveEntryName(field.getName()) + ": " + v.format(),
                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                          .hoverText(StringUtils.defaultString(v.description())));
            } else {
                ctx.sendSuccess(field.getName() + ": " + o);
            }
        }
    }
}
