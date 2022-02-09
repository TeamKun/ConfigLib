package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;

public abstract class Value<E, T extends Value<E, T>> {
    protected E value;
    private transient boolean listable = true;

    public Value(E value) {
        this.value = value;
    }

    public E value() {
        return value;
    }

    public void value(E value) {
        this.value = value;
    }

    protected boolean listable() {
        return listable;
    }

    public T listable(boolean listable) {
        this.listable = listable;
        return ((T) this);
    }

    protected abstract void sendListMessage(CommandContext ctx, String entryName);
}
