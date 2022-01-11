package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.annotation.Internal;

public interface Value<T> {
    T value();

    void value(T value);

    @Internal
    boolean listable();

    @Internal
    void sendListMessage(CommandContext ctx, String entryName);
}