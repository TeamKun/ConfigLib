package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;

public interface Value<T> {
    T value();

    void value(T value);

    boolean listable();

    void sendListMessage(CommandContext ctx, String entryName);

    void appendArgument(UsageBuilder builder);

    boolean isCorrectArgument(Object argument);

    T argumentToValue(Object argument);

    default String incorrectArgumentMessage(Object argument) {
        return argument + "は不正な引数です.";
    }
}