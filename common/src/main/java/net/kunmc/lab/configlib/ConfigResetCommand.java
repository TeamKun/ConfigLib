package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.store.ChangeTrace;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigResetCommand extends Command {
    public ConfigResetCommand(@NotNull Set<CommonBaseConfig> configs,
                              ConfigCommandDescriptions.Provider descriptions,
                              MaskedRevealPolicy maskedRevealPolicy) {
        super(SubCommandType.Reset.name);
        description(ConfigCommandDescriptions.reset(descriptions));

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }


        if (configs.size() == 1) {
            execute(ctx -> configs.forEach(config -> exec(ctx, config, descriptions, maskedRevealPolicy)));
        } else {
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                description(ConfigCommandDescriptions.resetConfig(descriptions, config.entryName()));
                execute(ctx -> exec(ctx, config, descriptions, maskedRevealPolicy));
            }}));
        }
    }

    private void exec(CommandContext ctx,
                      CommonBaseConfig config,
                      ConfigCommandDescriptions.Provider descriptions,
                      MaskedRevealPolicy maskedRevealPolicy) {
        try {
            config.mutate(() -> {
                config.schema()
                      .entries()
                      .forEach(config::resetEntryToDefault);
            }, ChangeTrace.command(ctx, "reset " + config.entryName()));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx, descriptions);
            return;
        }
        ctx.sendSuccess(descriptions.describe(ctx, ConfigCommandDescriptions.Key.RESET_SUCCESS, config.entryName()));
        ConfigListCommand.listFields(ctx, config, maskedRevealPolicy);
    }
}
