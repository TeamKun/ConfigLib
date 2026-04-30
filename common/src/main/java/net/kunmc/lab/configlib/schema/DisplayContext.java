package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.MaskedRevealPolicy;
import org.jetbrains.annotations.Nullable;

public final class DisplayContext {
    private static final String MASKED_TEXT = "<masked>";
    private static final DisplayContext RAW = new DisplayContext(null, null, MaskedRevealPolicy.DEFAULT);
    @Nullable
    private final CommandContext commandContext;
    @Nullable
    private final CommonBaseConfig config;
    private final MaskedRevealPolicy maskedRevealPolicy;

    private DisplayContext(@Nullable CommandContext commandContext,
                           @Nullable CommonBaseConfig config,
                           MaskedRevealPolicy maskedRevealPolicy) {
        this.commandContext = commandContext;
        this.config = config;
        this.maskedRevealPolicy = maskedRevealPolicy;
    }

    public static DisplayContext raw() {
        return RAW;
    }

    public static DisplayContext command(CommandContext ctx,
                                         CommonBaseConfig config,
                                         MaskedRevealPolicy maskedRevealPolicy) {
        return new DisplayContext(ctx, config, maskedRevealPolicy);
    }

    public static DisplayContext audit(CommandContext ctx,
                                       CommonBaseConfig config,
                                       MaskedRevealPolicy maskedRevealPolicy) {
        return new DisplayContext(ctx, config, maskedRevealPolicy);
    }

    public static DisplayContext diff(CommandContext ctx,
                                      CommonBaseConfig config,
                                      MaskedRevealPolicy maskedRevealPolicy) {
        return new DisplayContext(ctx, config, maskedRevealPolicy);
    }

    public static DisplayContext history(CommandContext ctx,
                                         CommonBaseConfig config,
                                         MaskedRevealPolicy maskedRevealPolicy) {
        return new DisplayContext(ctx, config, maskedRevealPolicy);
    }

    public boolean shouldMask(ConfigSchemaEntry<?> entry) {
        if (this == RAW) {
            return false;
        }
        return entry.isMasked() && !canRevealSensitive(entry);
    }

    public String display(String rawText, ConfigSchemaEntry<?> entry) {
        return shouldMask(entry) ? MASKED_TEXT : rawText;
    }

    private boolean canRevealSensitive(ConfigSchemaEntry<?> entry) {
        if (commandContext == null || config == null) {
            return false;
        }
        return maskedRevealPolicy.canReveal(commandContext, config, entry);
    }
}
