package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.schema.DisplayContext;
import net.kunmc.lab.configlib.store.AuditChange;
import net.kunmc.lab.configlib.store.AuditEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

class ConfigAuditCommand extends Command {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ConfigAuditCommand(@NotNull Set<CommonBaseConfig> configs) {
        super(SubCommandType.Audit.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() == 1) {
            CommonBaseConfig config = configs.iterator()
                                             .next();
            execute(ctx -> execList(ctx, config));
            argument(new IntegerArgument("index", 0, Integer.MAX_VALUE)).execute((index, ctx) -> execDetail(ctx,
                                                                                                            config,
                                                                                                            index));
            return;
        }

        configs.forEach(config -> addChildren(new Command(config.entryName()) {{
            execute(ctx -> execList(ctx, config));
            argument(new IntegerArgument("index", 0, Integer.MAX_VALUE)).execute((index, ctx) -> execDetail(ctx,
                                                                                                            config,
                                                                                                            index));
        }}));
    }

    private static void execList(CommandContext ctx, CommonBaseConfig config) {
        config.inspect(() -> {
            List<AuditEntry> audit = config.readAudit();
            if (audit.isEmpty()) {
                ctx.sendFailure(config.entryName() + " has no audit entries");
                return;
            }

            ctx.sendMessage(ConfigUtil.configHeader(config));
            for (int i = 0; i < audit.size(); i++) {
                AuditEntry entry = audit.get(i);
                ctx.sendSuccess("[" + i + "]: " + summary(entry));
            }
        });
    }

    private static void execDetail(CommandContext ctx, CommonBaseConfig config, int index) {
        config.inspect(() -> {
            List<AuditEntry> audit = config.readAudit();
            if (audit.isEmpty()) {
                ctx.sendFailure(config.entryName() + " has no audit entries");
                return;
            }
            if (index >= audit.size()) {
                ctx.sendFailure("Index " + index + " is out of range (0-" + (audit.size() - 1) + ")");
                return;
            }

            AuditEntry entry = audit.get(index);
            ctx.sendMessage(ConfigUtil.configHeader(config));
            ctx.sendSuccess("[" + index + "]: " + summary(entry));
            if (entry.trace()
                     .reason() != null) {
                ctx.sendSuccess("reason: " + entry.trace()
                                                  .reason());
            }
            if (entry.trace()
                     .actor()
                     .isKnown()) {
                ctx.sendSuccess("actor: " + actorText(entry));
            }
            if (!entry.trace()
                      .paths()
                      .isEmpty()) {
                ctx.sendSuccess("paths: " + String.join(", ",
                                                        entry.trace()
                                                             .paths()));
            }
            for (AuditChange change : entry.changes()) {
                ctx.sendSuccess(change.path() + ": " + displayChangeValue(config,
                                                                          ctx,
                                                                          change.path(),
                                                                          change.beforeText()) + " -> " + displayChangeValue(
                        config,
                        ctx,
                        change.path(),
                        change.afterText()));
            }
        });
    }

    static String summary(AuditEntry entry) {
        return dateText(entry) + " " + entry.trace()
                                            .source() + actorSuffix(entry) + reasonSuffix(entry) + pathsSuffix(entry);
    }

    private static String dateText(AuditEntry entry) {
        return DATE_FORMAT.format(new Date(entry.timestamp()));
    }

    private static String actorSuffix(AuditEntry entry) {
        if (!entry.trace()
                  .actor()
                  .isKnown()) {
            return "";
        }
        return " by " + actorText(entry);
    }

    private static String pathsSuffix(AuditEntry entry) {
        if (entry.trace()
                 .paths()
                 .isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(", ");
        entry.trace()
             .paths()
             .forEach(joiner::add);
        return " [" + joiner + "]";
    }

    private static String reasonSuffix(AuditEntry entry) {
        if (entry.trace()
                 .reason() == null) {
            return "";
        }
        return " (" + entry.trace()
                           .reason() + ")";
    }

    private static String actorText(AuditEntry entry) {
        if (entry.trace()
                 .actor()
                 .name() != null) {
            return entry.trace()
                        .actor()
                        .name();
        }
        return entry.trace()
                    .actor()
                    .uuid();
    }

    static String displayChangeValue(CommonBaseConfig config, CommandContext ctx, String path, String rawText) {
        ConfigSchemaEntry<?> entry = config.schema()
                                           .findEntry(path)
                                           .orElse(null);
        if (entry == null) {
            return rawText;
        }
        return DisplayContext.audit(ctx)
                             .display(rawText, entry);
    }
}
