package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.MapValue;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class Enum2ObjectMapValue<T extends Enum<T>, V, U extends Enum2ObjectMapValue<T, V, U>> extends MapValue<T, V, U> {
    private transient final Class<T> clazz;
    private transient Predicate<T> filter = x -> true;

    public Enum2ObjectMapValue(Map<T, V> value, T... t) {
        super(value);

        clazz = ((Class<T>) t.getClass()
                             .getComponentType());
    }

    public U setKeyFilter(Predicate<T> filter) {
        this.filter = filter;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected T argumentToKeyForPut(List<Object> argument, CommandContext ctx) {
        return clazz.cast(argument.get(0));
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, x -> value.containsKey(x));
    }

    @Override
    protected T argumentToKeyForRemove(List<Object> argument, CommandContext ctx) {
        return clazz.cast(argument.get(0));
    }

    @Override
    protected String keyToString(T t) {
        return t.name();
    }
}
