package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.Nullable;

public final class DisplayContext {
    private static final String MASKED_TEXT = "<masked>";
    private static final DisplayContext RAW = new DisplayContext(null);
    @Nullable
    private final CommandContext commandContext;

    private DisplayContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public static DisplayContext raw() {
        return RAW;
    }

    public static DisplayContext command(CommandContext ctx) {
        return new DisplayContext(ctx);
    }

    public static DisplayContext audit(CommandContext ctx) {
        return new DisplayContext(ctx);
    }

    public static DisplayContext diff(CommandContext ctx) {
        return new DisplayContext(ctx);
    }

    public static DisplayContext history(CommandContext ctx) {
        return new DisplayContext(ctx);
    }

    public static DisplayContext defaultValue(CommandContext ctx) {
        return new DisplayContext(ctx);
    }

    public boolean shouldMask(ConfigSchemaEntry<?> entry) {
        if (this == RAW) {
            return false;
        }
        return entry.isMasked() && !canRevealSensitive();
    }

    public String display(String rawText, ConfigSchemaEntry<?> entry) {
        return shouldMask(entry) ? MASKED_TEXT : rawText;
    }

    public boolean canRevealSensitive() {
        // TODO Introduce sender-aware reveal rules, e.g. console/op/permission-based access.
        return false;
    }
}
