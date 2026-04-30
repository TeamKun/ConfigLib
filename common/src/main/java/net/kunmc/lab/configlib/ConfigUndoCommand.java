package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.store.ChangeTrace;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigUndoCommand extends Command {
    public ConfigUndoCommand(@NotNull Set<CommonBaseConfig> configs,
                             ConfigCommandDescriptions.Provider descriptions,
                             MaskedRevealPolicy maskedRevealPolicy) {
        super(SubCommandType.Undo.name);
        description(ConfigCommandDescriptions.undo(descriptions));

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        // /config undo - restore history[1]
        execute(ctx -> configs.forEach(config -> exec(ctx, config, 1, descriptions, maskedRevealPolicy)));

        // /config undo <index> - restore history[index]
        argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).description(ConfigCommandDescriptions.undoIndex(
                                                                            descriptions))
                                                                    .execute((index, ctx) -> {
                                                                        configs.forEach(config -> exec(ctx,
                                                                                                       config,
                                                                                                       index,
                                                                                                       descriptions,
                                                                                                       maskedRevealPolicy));
                                                                    });

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    description(ConfigCommandDescriptions.undoConfig(descriptions, config.entryName()));
                    execute(ctx -> exec(ctx, config, 1, descriptions, maskedRevealPolicy));
                    argument(new IntegerArgument("index",
                                                 1,
                                                 Integer.MAX_VALUE)).description(ConfigCommandDescriptions.undoIndex(
                                                                            descriptions))
                                                                    .execute((index, ctx) -> {
                                                                        exec(ctx,
                                                                             config,
                                                                             index,
                                                                             descriptions,
                                                                             maskedRevealPolicy);
                                                                    });
                }});
            });
        }
    }

    static void exec(CommandContext ctx,
                     CommonBaseConfig config,
                     int index,
                     ConfigCommandDescriptions.Provider descriptions,
                     MaskedRevealPolicy maskedRevealPolicy) {
        if (config.inspect(() -> config.readHistory()
                                       .size()) <= 1) {
            ctx.sendFailure(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.UNDO_NO_RESTORABLE_HISTORY,
                                                  config.entryName()));
            return;
        }

        boolean applied;
        try {
            applied = config.applyUndo(index,
                                       ChangeTrace.command(ctx,
                                                           "undo " + config.entryName(),
                                                           "history[" + index + "]"));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx, descriptions);
            return;
        }

        if (applied) {
            ctx.sendSuccess(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.UNDO_SUCCESS,
                                                  config.entryName(),
                                                  index));
            ConfigListCommand.listFields(ctx, config, maskedRevealPolicy);
        } else {
            ctx.sendFailure(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.UNDO_NOT_RESTORABLE,
                                                  index,
                                                  config.entryName()));
        }
    }
}
