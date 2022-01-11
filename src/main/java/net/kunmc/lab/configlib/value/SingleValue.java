package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SingleValue<T> extends Value<T> {
    @Internal
    boolean validateOnSet(T newValue);

    @Internal
    void onSetValue(T newValue);

    @Internal
    boolean writableByCommand();

    @Internal
    void appendArgument(UsageBuilder builder);

    @Internal
    boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    @Internal
    T argumentToValue(List<Object> argument, CommandSender sender);

    @Internal
    default String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Internal
    default String invalidValueMessage(String entryName, T newValue) {
        return "引数の値が不正です.";
    }

    @Internal
    default String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    @Internal
    default void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}
