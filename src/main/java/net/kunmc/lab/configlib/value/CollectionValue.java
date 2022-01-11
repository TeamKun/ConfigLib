package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.Collection;

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

    boolean isCorrectArgumentForAdd(Object argument, CommandSender sender);

    boolean isCorrectArgumentForRemove(Object argument, CommandSender sender);

    T argumentToValueForAdd(Object argument, CommandSender sender);

    T argumentToValueForRemove(Object argument, CommandSender sender);

    default String incorrectArgumentMessageForAdd(Object argument) {
        return argument + "は不正な引数です.";
    }

    default String incorrectArgumentMessageForRemove(Object argument) {
        return argument + "は不正な引数です.";
    }
}

