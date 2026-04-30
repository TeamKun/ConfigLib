package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.ConfigCommandDescriptions;

public class CollectionValueAddCommandMessageParameter<T> {
    private final String entryName;
    private final CommandContext ctx;
    private final T added;
    private final ConfigCommandDescriptions.Provider descriptions;

    public CollectionValueAddCommandMessageParameter(String entryName, CommandContext ctx, T added) {
        this(entryName, ctx, added, ConfigCommandDescriptions.defaultProvider());
    }

    public CollectionValueAddCommandMessageParameter(String entryName,
                                                     CommandContext ctx,
                                                     T added,
                                                     ConfigCommandDescriptions.Provider descriptions) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.added = added;
        this.descriptions = descriptions;
    }

    public String entryName() {
        return entryName;
    }

    public CommandContext ctx() {
        return ctx;
    }

    public T added() {
        return added;
    }

    public String describe(ConfigCommandDescriptions.Key key, Object... args) {
        return descriptions.describe(ctx, key, args);
    }
}
