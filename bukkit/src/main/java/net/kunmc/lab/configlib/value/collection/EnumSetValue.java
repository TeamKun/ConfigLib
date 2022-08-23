package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EnumSetValue<T extends Enum<T>> extends SetValue<T, EnumSetValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public EnumSetValue(T... values) {
        this(x -> true, values);
    }

    public EnumSetValue(@NotNull Predicate<T> filter, T... values) {
        super(new HashSet<>());

        this.clazz = ((Class<T>) values.getClass().getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, x -> !value.contains(x) && filter.test(x));
    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(clazz.cast(argument.get(0)));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, x -> value.contains(x));
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(clazz.cast(argument.get(0)));
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
