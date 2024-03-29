package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class EnumListValue<T extends Enum<T>> extends ListValue<T, EnumListValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public EnumListValue(T... values) {
        this(x -> true, values);
    }

    public EnumListValue(@NotNull Predicate<T> filter, T... values) {
        super(new ArrayList<>());

        this.clazz = ((Class<T>) values.getClass()
                                       .getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected List<T> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return Collections.singletonList(clazz.cast(argument.get(0)));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, x -> value.contains(x));
    }

    @Override
    protected List<T> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Collections.singletonList(clazz.cast(argument.get(0)));
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
