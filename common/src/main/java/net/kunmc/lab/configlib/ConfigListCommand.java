package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.apache.commons.lang3.StringUtils;

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
        config.inspect(() -> {
            ctx.sendMessage(ConfigUtil.configHeader(config));
            for (ConfigSchemaEntry<?> entry : config.schema()
                                                    .entries()) {
                ctx.sendMessageWithOption(entry.entryName() + ": " + entry.displayString(),
                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                          .hoverText(StringUtils.defaultString(entry.metadata()
                                                                                                    .description())));
            }
        });
    }
}
