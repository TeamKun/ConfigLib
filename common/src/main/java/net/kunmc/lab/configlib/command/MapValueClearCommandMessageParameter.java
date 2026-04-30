package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.ConfigCommandDescriptions;

public class MapValueClearCommandMessageParameter {
    private final String entryName;
    private final CommandContext ctx;
    private final ConfigCommandDescriptions.Provider descriptions;

    public MapValueClearCommandMessageParameter(String entryName, CommandContext ctx) {
        this(entryName, ctx, ConfigCommandDescriptions.defaultProvider());
    }

    public MapValueClearCommandMessageParameter(String entryName,
                                                CommandContext ctx,
                                                ConfigCommandDescriptions.Provider descriptions) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.descriptions = descriptions;
    }

    public String entryName() {
        return entryName;
    }

    public CommandContext ctx() {
        return ctx;
    }

    public String describe(ConfigCommandDescriptions.Key key, Object... args) {
        return descriptions.describe(ctx, key, args);
    }
}
