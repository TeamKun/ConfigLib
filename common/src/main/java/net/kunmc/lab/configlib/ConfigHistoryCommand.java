package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.HistoryEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

class ConfigHistoryCommand extends Command {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ConfigHistoryCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.History.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() > 1) {
            // /config history <index>              - show detail for all configs
            // /config history <name>               - list entries for that config
            // /config history <name> <index>       - show detail for that config
            // /config history <name> diff <index>  - diff current vs history[index]
            // /config history <name> undo          - undo 1 step
            // /config history <name> undo <N>      - undo N steps
            argument(new IntegerArgument("index", 0, Integer.MAX_VALUE)).execute((index, ctx) -> {
                configs.forEach(config -> execDetail(ctx, config, index));
            });
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                execute(ctx -> execList(ctx, config));
                argument(new IntegerArgument("index", 0, Integer.MAX_VALUE)).execute((index, ctx) -> {
                    execDetail(ctx, config, index);
                });
                addChildren(new Command("diff") {{
                    argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
                        ConfigDiffCommand.execDiff(ctx, config, 0, index);
                    });
                    argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                             new IntegerArgument("index2", 0, Integer.MAX_VALUE)).execute((index1, index2, ctx) -> {
                        ConfigDiffCommand.execDiff(ctx, config, index1, index2);
                    });
                }});
                addChildren(new Command("undo") {{
                    execute(ctx -> ConfigUndoCommand.exec(ctx, config, 1));
                    argument(new IntegerArgument("steps", 1, Integer.MAX_VALUE)).execute((steps, ctx) -> {
                        ConfigUndoCommand.exec(ctx, config, steps);
                    });
                }});
            }}));
        } else {
            // /config history              - list all entries
            // /config history <index>      - show detail
            // /config history diff <index> - diff current vs history[index]
            // /config history undo         - undo 1 step
            // /config history undo <N>     - undo N steps
            CommonBaseConfig config = configs.iterator()
                                             .next();
            execute(ctx -> execList(ctx, config));
            argument(new IntegerArgument("index", 0, Integer.MAX_VALUE)).execute((index, ctx) -> {
                execDetail(ctx, config, index);
            });
            addChildren(new Command("diff") {{
                argument(new IntegerArgument("index", 1, Integer.MAX_VALUE)).execute((index, ctx) -> {
                    ConfigDiffCommand.execDiff(ctx, config, 0, index);
                });
                argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                         new IntegerArgument("index2", 0, Integer.MAX_VALUE)).execute((index1, index2, ctx) -> {
                    ConfigDiffCommand.execDiff(ctx, config, index1, index2);
                });
            }});
            addChildren(new Command("undo") {{
                execute(ctx -> ConfigUndoCommand.exec(ctx, config, 1));
                argument(new IntegerArgument("steps", 1, Integer.MAX_VALUE)).execute((steps, ctx) -> {
                    ConfigUndoCommand.exec(ctx, config, steps);
                });
            }});
        }
    }

    private static void execList(CommandContext ctx, CommonBaseConfig config) {
        config.inspect(() -> {
            List<HistoryEntry> history = config.readHistory();
            if (history.isEmpty()) {
                ctx.sendSuccess(config.entryName() + "の変更履歴はありません");
                return;
            }
            ctx.sendMessage(ConfigUtil.configHeader(config));
            for (int i = 0; i < history.size(); i++) {
                HistoryEntry entry = history.get(i);
                boolean isLatest = (i == 0);
                String dateStr = entry.timestamp() > 0 ? DATE_FORMAT.format(new Date(entry.timestamp())) : "不明";
                String label = "[" + i + "]: " + dateStr + (isLatest ? " (最新)" : "");
                String hoverText = buildFieldsText(config, entry.config());
                ctx.sendMessageWithOption(label, opt -> {
                    opt.rgb(ChatColorUtil.GREEN.getRGB());
                    if (!hoverText.isEmpty()) {
                        opt.hoverText(hoverText);
                    }
                });
            }
        });
    }

    private static void execDetail(CommandContext ctx, CommonBaseConfig config, int index) {
        config.inspect(() -> {
            List<HistoryEntry> history = config.readHistory();
            if (history.isEmpty()) {
                ctx.sendFailure(config.entryName() + "の変更履歴がありません");
                return;
            }
            if (index >= history.size()) {
                ctx.sendFailure("インデックス " + index + " は範囲外です (0–" + (history.size() - 1) + ")");
                return;
            }
            HistoryEntry entry = history.get(index);
            String dateStr = entry.timestamp() > 0 ? DATE_FORMAT.format(new Date(entry.timestamp())) : "不明";
            ctx.sendMessage(ConfigUtil.configHeader(config));
            ctx.sendSuccess("[" + index + "]: " + dateStr);
            listFields(ctx, config, entry.config());
        });
    }

    private static String buildFieldsText(CommonBaseConfig liveConfig, CommonBaseConfig histConfig) {
        StringBuilder sb = new StringBuilder();
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(liveConfig.getClass())) {
            if (!ConfigUtil.isConfigFieldModifier(field)) {
                continue;
            }
            ConfigSchemaEntry<?> entry = liveConfig.schema()
                                                   .findEntry(field.getName())
                                                   .orElse(null);
            if (entry == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object hist = field.get(histConfig);
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(entry.entryName())
                  .append(": ")
                  .append(entry.displayString(hist));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }

    private static void listFields(CommandContext ctx, CommonBaseConfig liveConfig, CommonBaseConfig histConfig) {
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(liveConfig.getClass())) {
            if (!ConfigUtil.isConfigFieldModifier(field)) {
                continue;
            }
            ConfigSchemaEntry<?> entry = liveConfig.schema()
                                                   .findEntry(field.getName())
                                                   .orElse(null);
            if (entry == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object hist = field.get(histConfig);
                ctx.sendSuccess(entry.entryName() + ": " + entry.displayString(hist));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
