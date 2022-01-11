package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public interface CollectionValue<T extends Collection<E>, E> extends Value<T> {
    boolean addableByCommand();

    boolean removableByCommand();

    boolean clearableByCommand();

    boolean validateForAdd(T element);

    boolean validateForRemove(T element);

    String invalidValueMessageForAdd(String entryName, T element);

    String succeedMessageForAdd(String entryName, T element);

    String invalidValueMessageForRemove(String entryName, T element);

    String succeedMessageForRemove(String entryName, T element);

    String clearMessage(String entryName);

    void appendArgumentForAdd(UsageBuilder builder);

    void appendArgumentForRemove(UsageBuilder builder);

    boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender);

    boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender);

    T argumentToValueForAdd(List<Object> argument, CommandSender sender);

    T argumentToValueForRemove(List<Object> argument, CommandSender sender);

    default String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    default String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }
}

