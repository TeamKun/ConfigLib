package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigResetCommand extends Command {
    public ConfigResetCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Reset.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }


        if (configs.size() == 1) {
            execute(ctx -> configs.forEach(config -> exec(ctx, config)));
        } else {
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                execute(ctx -> exec(ctx, config));
            }}));
        }
    }

    private void exec(CommandContext ctx, CommonBaseConfig config) {
        try {
            config.mutate(() -> ConfigUtil.getValues(config)
                                          .forEach(Value::resetToDefault));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx);
            return;
        }
        ctx.sendSuccess(config.entryName() + "をデフォルト値にリセットしました");
        ConfigListCommand.listFields(ctx, config);
    }
}
