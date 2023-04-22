package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.exception.LoadingConfigInvalidValueException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigReloadCommand extends Command {
    public ConfigReloadCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Reload.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        execute(ctx -> {
            configs.forEach(config -> {
                exec(ctx, config);
            });
        });

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    execute(ctx -> {
                        exec(ctx, config);
                    });
                }});
            });
        }
    }

    private void exec(CommandContext ctx, CommonBaseConfig config) {
        String fileName = config.getConfigFile()
                                .getName();
        try {
            if (config.loadConfig()) {
                ctx.sendSuccess(fileName + "を再読み込みしました");
            } else {
                ctx.sendFailure(fileName + "の読み込みに失敗しました");
            }
        } catch (LoadingConfigInvalidValueException e) {
            ctx.sendFailure(fileName + "の読み込みに失敗しました");
            e.printStackTrace();
        }
    }
}
