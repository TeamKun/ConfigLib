package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.schema.DisplayContext;

final class MaskedCommandOutput {
    private MaskedCommandOutput() {
    }

    static boolean shouldMask(CommandContext ctx,
                              CommonBaseConfig config,
                              ConfigSchemaEntry<?> entry,
                              MaskedRevealPolicy policy) {
        return DisplayContext.command(ctx, config, policy)
                             .shouldMask(entry);
    }

    static String text(CommandContext ctx,
                       CommonBaseConfig config,
                       ConfigSchemaEntry<?> entry,
                       MaskedRevealPolicy policy) {
        return DisplayContext.command(ctx, config, policy)
                             .display("", entry);
    }
}
