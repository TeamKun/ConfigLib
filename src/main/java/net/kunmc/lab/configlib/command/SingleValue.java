package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class SingleValue<T> extends Value<T> {
    public SingleValue(T value) {
        super(value);
    }

    protected abstract boolean writableByCommand();

    protected abstract void appendArgument(UsageBuilder builder);

    protected abstract boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    protected abstract String incorrectArgumentMessage(List<Object> argument);

    protected abstract T argumentToValue(List<Object> argument, CommandSender sender);

    protected abstract boolean validateOnSet(T newValue);

    protected abstract String invalidValueMessage(String entryName, T newValue);

    protected abstract void onSetValue(T newValue);

    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}