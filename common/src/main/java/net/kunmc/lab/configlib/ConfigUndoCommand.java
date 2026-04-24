package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.store.ChangeTrace;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigUndoCommand extends Command {
    public ConfigUndoCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Undo.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        // /config undo - restore history[1]
        execute(ctx -> configs.forEach(config -> exec(ctx, config, 1)));

        // /config undo <index> - restore history[index]
        argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
            configs.forEach(config -> exec(ctx, config, index));
        });

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    execute(ctx -> exec(ctx, config, 1));
                    argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
                        exec(ctx, config, index);
                    });
                }});
            });
        }
    }

    static void exec(CommandContext ctx, CommonBaseConfig config, int index) {
        if (config.inspect(() -> config.readHistory()
                                       .size()) <= 1) {
            ctx.sendFailure("No restorable history entries for " + config.entryName());
            return;
        }

        boolean applied;
        try {
            applied = config.applyUndo(index,
                                       ChangeTrace.command(ctx,
                                                           "undo " + config.entryName(),
                                                           "history[" + index + "]"));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx);
            return;
        }

        if (applied) {
            ctx.sendSuccess(config.entryName() + " restored history[" + index + "]");
            ConfigListCommand.listFields(ctx, config);
        } else {
            ctx.sendFailure("History index " + index + " is not restorable for " + config.entryName());
        }
    }
}
