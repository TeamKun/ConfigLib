package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class ConfigUndoCommand extends Command {
    public ConfigUndoCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Undo.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        // /config undo — go back 1 step
        execute(ctx -> configs.forEach(config -> exec(ctx, config, 1)));

        // /config undo <N> — go back N steps
        argument(new IntegerArgument("steps", 1, Integer.MAX_VALUE), (steps, ctx) -> {
            configs.forEach(config -> exec(ctx, config, steps));
        });

        if (configs.size() > 1) {
            configs.forEach(config -> {
                addChildren(new Command(config.entryName()) {{
                    execute(ctx -> exec(ctx, config, 1));
                    argument(new IntegerArgument("steps", 1, Integer.MAX_VALUE), (steps, ctx) -> {
                        exec(ctx, config, steps);
                    });
                }});
            });
        }
    }

    static void exec(CommandContext ctx, CommonBaseConfig config, int stepsBack) {
        if (config.applyUndo(stepsBack)) {
            ctx.sendSuccess(config.entryName() + "を" + stepsBack + "つ前の状態に戻しました");
            ConfigListCommand.listFields(ctx, config);
        } else {
            ctx.sendFailure(config.entryName() + "の変更履歴がありません");
        }
    }
}
