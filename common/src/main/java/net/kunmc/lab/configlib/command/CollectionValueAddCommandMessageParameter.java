package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;

public class CollectionValueAddCommandMessageParameter<T> {
    private final String entryName;
    private final CommandContext ctx;
    private final T added;

    public CollectionValueAddCommandMessageParameter(String entryName, CommandContext ctx, T added) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.added = added;
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
}
