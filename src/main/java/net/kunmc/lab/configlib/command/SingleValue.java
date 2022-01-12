package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SingleValue<T> extends Value<T> {
    public SingleValue(T value) {
        super(value);
    }

    protected abstract void onSetValue(T newValue);

    protected abstract boolean validateOnSet(T newValue);

    protected abstract boolean writableByCommand();

    protected abstract void appendArgument(UsageBuilder builder);

    protected abstract boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    protected abstract T argumentToValue(List<Object> argument, CommandSender sender);

    protected String invalidValueMessage(String entryName, T newValue) {
        return "引数の値が不正です.";
    }

    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}