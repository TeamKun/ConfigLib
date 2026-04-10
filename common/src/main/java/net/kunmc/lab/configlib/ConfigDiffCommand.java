package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.store.HistoryEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

class ConfigDiffCommand extends Command {
    public ConfigDiffCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Diff.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() > 1) {
            // /config diff <name> <index>
            // /config diff <name> <index1> <index2>
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                argument(new IntegerArgument("index", 1, Integer.MAX_VALUE), (index, ctx) -> {
                    execDiff(ctx, config, 0, index);
                });
                argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                         new IntegerArgument("index2", 0, Integer.MAX_VALUE),
                         (index1, index2, ctx) -> {
                             execDiff(ctx, config, index1, index2);
                         });
            }}));
        } else {
            // /config diff <index>
            // /config diff <index1> <index2>
            CommonBaseConfig config = configs.iterator()
                                             .next();
            argument(new IntegerArgument("index", 1, Integer.MAX_VALUE), (index, ctx) -> {
                execDiff(ctx, config, 0, index);
            });
            argument(new IntegerArgument("index1", 0, Integer.MAX_VALUE),
                     new IntegerArgument("index2", 0, Integer.MAX_VALUE),
                     (index1, index2, ctx) -> {
                         execDiff(ctx, config, index1, index2);
                     });
        }
    }

    static void execDiff(CommandContext ctx, CommonBaseConfig config, int index1, int index2) {
        if (index1 == index2) {
            ctx.sendFailure("同じインデックスを指定しています");
            return;
        }

        List<HistoryEntry> history = config.readHistory();
        if (history.size() <= 1) {
            ctx.sendFailure(config.entryName() + "の変更履歴がありません");
            return;
        }

        int maxIndex = Math.max(index1, index2);
        if (maxIndex >= history.size()) {
            ctx.sendFailure("インデックス " + maxIndex + " は範囲外です (0–" + (history.size() - 1) + ")");
            return;
        }

        // 古い方から新しい方へ: older(大インデックス) → newer(小インデックス)
        int olderIndex = Math.max(index1, index2);
        int newerIndex = Math.min(index1, index2);
        CommonBaseConfig olderConfig = history.get(olderIndex)
                                              .config();
        CommonBaseConfig newerConfig = history.get(newerIndex)
                                              .config();

        ctx.sendMessage(ConfigUtil.configHeader(config));
        ctx.sendSuccess("[" + olderIndex + "] → [" + newerIndex + "]");

        boolean anyDiff = false;
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object older = field.get(olderConfig);
                Object newer = field.get(newerConfig);

                if (older instanceof Value && newer instanceof Value) {
                    // liveConfigのValue名を使う
                    String name = ((Value<?, ?>) field.get(config)).resolveEntryName(field.getName());
                    String olderFmt = ((Value<?, ?>) older).displayString();
                    String newerFmt = ((Value<?, ?>) newer).displayString();
                    if (olderFmt.equals(newerFmt)) {
                        continue;
                    }
                    anyDiff = true;
                    String line = name + ": " + olderFmt + " → " + newerFmt;
                    ctx.sendMessageWithOption(line, opt -> opt.rgb(ChatColorUtil.GREEN.getRGB()));
                } else {
                    String olderFmt = String.valueOf(older);
                    String newerFmt = String.valueOf(newer);
                    if (olderFmt.equals(newerFmt)) {
                        continue;
                    }
                    anyDiff = true;
                    ctx.sendSuccess(field.getName() + ": " + olderFmt + " → " + newerFmt);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if (!anyDiff) {
            ctx.sendSuccess("差分はありません");
        }
    }
}
