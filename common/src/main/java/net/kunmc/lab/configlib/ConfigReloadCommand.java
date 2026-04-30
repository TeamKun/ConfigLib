package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigReloadCommand extends Command {
    public ConfigReloadCommand(@NotNull Set<CommonBaseConfig> configs,
                               ConfigCommandDescriptions.Provider descriptions,
                               MaskedRevealPolicy maskedRevealPolicy) {
        super(SubCommandType.Reload.name);
        description(ConfigCommandDescriptions.reload(descriptions));

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        execute(ctx -> {
            configs.forEach(config -> {
                exec(ctx, config, descriptions);
            });
        });

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    description(ConfigCommandDescriptions.reloadConfig(descriptions, config.entryName()));
                    execute(ctx -> {
                        exec(ctx, config, descriptions);
                    });
                }});
            });
        }
    }

    private void exec(CommandContext ctx, CommonBaseConfig config, ConfigCommandDescriptions.Provider descriptions) {
        try {
            if (config.loadConfig()) {
                ctx.sendSuccess(descriptions.describe(ctx,
                                                      ConfigCommandDescriptions.Key.RELOAD_SUCCESS,
                                                      config.entryName()));
            } else {
                ctx.sendFailure(descriptions.describe(ctx,
                                                      ConfigCommandDescriptions.Key.RELOAD_FAILURE,
                                                      config.entryName()));
            }
        } catch (ConfigValidationException e) {
            ctx.sendFailure(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.RELOAD_FAILURE,
                                                  config.entryName()));
            e.sendMessage(ctx, descriptions);
        }
    }
}
