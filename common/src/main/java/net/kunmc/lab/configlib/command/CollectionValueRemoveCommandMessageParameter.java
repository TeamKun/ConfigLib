package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.ConfigCommandDescriptions;

public class CollectionValueRemoveCommandMessageParameter<T> {
    private final String entryName;
    private final CommandContext ctx;
    private final T removed;
    private final ConfigCommandDescriptions.Provider descriptions;

    public CollectionValueRemoveCommandMessageParameter(String entryName, CommandContext ctx, T removed) {
        this(entryName, ctx, removed, ConfigCommandDescriptions.defaultProvider());
    }

    public CollectionValueRemoveCommandMessageParameter(String entryName,
                                                        CommandContext ctx,
                                                        T removed,
                                                        ConfigCommandDescriptions.Provider descriptions) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.removed = removed;
        this.descriptions = descriptions;
    }

    public String entryName() {
        return entryName;
    }

    public CommandContext ctx() {
        return ctx;
    }

    public T removed() {
        return removed;
    }

    public String describe(ConfigCommandDescriptions.Key key, Object... args) {
        return descriptions.describe(ctx, key, args);
    }
}
