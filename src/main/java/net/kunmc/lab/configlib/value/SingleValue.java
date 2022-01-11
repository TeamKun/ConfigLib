package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface SingleValue<T> extends Value<T> {
    boolean validateOnSet(T newValue);

    void onSetValue(T newValue);

    boolean writableByCommand();

    void appendArgument(UsageBuilder builder);

    boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    T argumentToValue(List<Object> argument, CommandSender sender);

    default String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    default String invalidValueMessage(String entryName, T newValue) {
        return "引数の値が不正です.";
    }

    default String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    default void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}
