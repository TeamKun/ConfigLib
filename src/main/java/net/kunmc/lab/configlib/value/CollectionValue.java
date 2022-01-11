package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public interface CollectionValue<T extends Collection<E>, E> extends Value<T> {
    @Internal
    boolean addableByCommand();

    @Internal
    boolean removableByCommand();

    @Internal
    boolean clearableByCommand();

    @Internal
    boolean validateForAdd(T element);

    @Internal
    boolean validateForRemove(T element);

    @Internal
    String invalidValueMessageForAdd(String entryName, T element);

    @Internal
    String succeedMessageForAdd(String entryName, T element);

    @Internal
    String invalidValueMessageForRemove(String entryName, T element);

    @Internal
    String succeedMessageForRemove(String entryName, T element);

    @Internal
    String clearMessage(String entryName);

    @Internal
    void appendArgumentForAdd(UsageBuilder builder);

    @Internal
    void appendArgumentForRemove(UsageBuilder builder);

    @Internal
    boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender);

    @Internal
    boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender);

    @Internal
    T argumentToValueForAdd(List<Object> argument, CommandSender sender);

    @Internal
    T argumentToValueForRemove(List<Object> argument, CommandSender sender);

    @Internal
    default String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Internal
    default String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }
}

