package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public abstract class CollectionValue<T extends Collection<E>, E> extends Value<T> {
    public CollectionValue(T value) {
        super(value);
    }

    protected abstract boolean addableByCommand();

    protected abstract void appendArgumentForAdd(UsageBuilder builder);

    protected abstract boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender);

    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    protected abstract T argumentToValueForAdd(List<Object> argument, CommandSender sender);

    protected abstract boolean validateForAdd(T value);

    protected abstract String invalidValueMessageForAdd(String entryName, T value);

    protected abstract String succeedMessageForAdd(String entryName, T value);

    protected abstract boolean removableByCommand();

    protected abstract void appendArgumentForRemove(UsageBuilder builder);

    protected abstract boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender);

    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    protected abstract T argumentToValueForRemove(List<Object> argument, CommandSender sender);

    protected abstract boolean validateForRemove(T value);

    protected abstract String invalidValueMessageForRemove(String entryName, T value);

    protected abstract String succeedMessageForRemove(String entryName, T value);

    protected abstract boolean clearableByCommand();

    protected abstract String clearMessage(String entryName);
}
