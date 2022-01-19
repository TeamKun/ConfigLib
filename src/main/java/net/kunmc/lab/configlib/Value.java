package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;

public abstract class Value<T> {
    protected T value;

    public Value(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    public void value(T value) {
        this.value = value;
    }

    protected abstract boolean listable();

    protected abstract void sendListMessage(CommandContext ctx, String entryName);
}
