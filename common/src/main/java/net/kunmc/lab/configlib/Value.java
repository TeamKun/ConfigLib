package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public abstract class Value<E, T extends Value<E, T>> {
    protected E value;
    private String description;
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

    public String description() {
        return description;
    }

    public T description(String description) {
        this.description = description;
        return ((T) this);
    }

    protected boolean listable() {
        return listable;
    }

    public T listable(boolean listable) {
        this.listable = listable;
        return ((T) this);
    }

    protected abstract List<String> listMessages(CommandContext ctx, String entryName);
}
