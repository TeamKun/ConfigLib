package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.HistoryEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

class ConfigDiffCommand extends Command {
    private static final String DIFF_ARROW = " -> ";

    public ConfigDiffCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Diff.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() > 1) {
            // /config diff <name> default
            // /config diff <name> <index>
            // /config diff <name> <index1> <index2>
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                addChildren(new Command("default") {{
                    execute(ctx -> execDefaultDiff(ctx, config));
                }});
                argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
                    execDiff(ctx, config, 0, index);
                });
                argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                         new IntegerArgument("index2", 0, Integer.MAX_VALUE)).execute((index1, index2, ctx) -> {
                    execDiff(ctx, config, index1, index2);
                });
            }}));
            return;
        }

        // /config diff default
        // /config diff <index>
        // /config diff <index1> <index2>
        CommonBaseConfig config = configs.iterator()
                                         .next();
        addChildren(new Command("default") {{
            execute(ctx -> execDefaultDiff(ctx, config));
        }});
        argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
            execDiff(ctx, config, 0, index);
        });
        argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                 new IntegerArgument("index2", 0, Integer.MAX_VALUE)).execute((index1, index2, ctx) -> {
            execDiff(ctx, config, index1, index2);
        });
    }

    static void execDiff(CommandContext ctx, CommonBaseConfig config, int index1, int index2) {
        config.inspect(() -> {
            if (index1 == index2) {
                ctx.sendFailure("Cannot diff the same index");
                return;
            }

            List<HistoryEntry> history = config.readHistory();
            if (history.size() <= 1) {
                ctx.sendFailure("Not enough history entries for " + config.entryName());
                return;
            }

            int maxIndex = Math.max(index1, index2);
            if (maxIndex >= history.size()) {
                ctx.sendFailure("Index " + maxIndex + " is out of range (0-" + (history.size() - 1) + ")");
                return;
            }

            int olderIndex = Math.max(index1, index2);
            int newerIndex = Math.min(index1, index2);
            CommonBaseConfig olderConfig = history.get(olderIndex)
                                                  .config();
            CommonBaseConfig newerConfig = history.get(newerIndex)
                                                  .config();

            ctx.sendMessage(ConfigUtil.configHeader(config));
            ctx.sendSuccess("[" + olderIndex + "]" + DIFF_ARROW + "[" + newerIndex + "]");
            emitDiff(ctx, config, olderConfig, newerConfig);
        });
    }

    static void execDefaultDiff(CommandContext ctx, CommonBaseConfig config) {
        config.inspect(() -> {
            ctx.sendMessage(ConfigUtil.configHeader(config));
            ctx.sendSuccess("[default]" + DIFF_ARROW + "[current]");

            boolean anyDiff = false;
            for (ConfigSchemaEntry<?> entry : config.schema()
                                                    .entries()) {
                String defaultFmt = config.formatDefaultValue(entry);
                String currentFmt = entry.displayString();
                if (defaultFmt.equals(currentFmt)) {
                    continue;
                }
                anyDiff = true;
                ctx.sendSuccess(entry.entryName() + ": " + defaultFmt + DIFF_ARROW + currentFmt);
            }

            if (!anyDiff) {
                ctx.sendSuccess("No differences found");
            }
        });
    }

    private static void emitDiff(CommandContext ctx,
                                 CommonBaseConfig liveConfig,
                                 CommonBaseConfig olderConfig,
                                 CommonBaseConfig newerConfig) {
        boolean anyDiff = false;
        for (ConfigSchemaEntry<?> entry : liveConfig.schema()
                                                    .entries()) {
            Object older = entry.get(olderConfig);
            Object newer = entry.get(newerConfig);

            String olderFmt = entry.displayString(older);
            String newerFmt = entry.displayString(newer);
            if (olderFmt.equals(newerFmt)) {
                continue;
            }
            anyDiff = true;
            ctx.sendSuccess(entry.entryName() + ": " + olderFmt + DIFF_ARROW + newerFmt);
        }

        if (!anyDiff) {
            ctx.sendSuccess("No differences found");
        }
    }
}
