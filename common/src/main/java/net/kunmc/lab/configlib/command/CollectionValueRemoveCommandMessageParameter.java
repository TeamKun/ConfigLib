package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;

public class CollectionValueRemoveCommandMessageParameter<T> {
    private final String entryName;
    private final CommandContext ctx;
    private final T removed;

    public CollectionValueRemoveCommandMessageParameter(String entryName, CommandContext ctx, T removed) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.removed = removed;
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
}
