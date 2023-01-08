package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Set;

class ConfigGetCommand extends Command {
    public ConfigGetCommand(Set<CommonBaseConfig> configs) {
        super(SubCommandType.Get.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() == 1) {
            configs.forEach(config -> {
                init(config, this);
            });
        } else {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    init(config, this);
                }});
            });
        }
    }

    private void init(CommonBaseConfig config, Command command) {
        for (Field field : config.getClass()
                                 .getDeclaredFields()) {
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
                    execute(ctx -> ctx.sendMessageWithOption(field.getName() + ": " + v.asString(ctx),
                                                             option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                             .hoverText(StringUtils.defaultString(v.description()))));
                }});
            } else {
                command.addChildren(new Command(field.getName()) {{
                    execute(ctx -> ctx.sendSuccess(field.getName() + ": " + o));
                }});
            }
        }
    }
}
