package net.kunmc.lab.value;

import dev.kotx.flylib.command.CommandContext;

public interface Value<T> {
    T value();

    void value(T value);

    boolean listable();

    void sendListMessage(CommandContext ctx, String entryName);
}