package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Enum2EnumMapValue<K extends Enum<K>, V extends Enum<V>> extends Enum2ObjectMapValue<K, V, Enum2EnumMapValue<K, V>> {
    private final transient Class<V> clazz;
    private transient Predicate<V> filter = x -> true;

    public Enum2EnumMapValue(Class<K> keyClass, Class<V> valueClass) {
        super(new HashMap<>(), keyClass.getEnumConstants());
        this.clazz = valueClass;
    }

    public Enum2EnumMapValue<K, V> setValueFilter(Predicate<V> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.enumArgument("value", clazz, filter);
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return argument.get(1) + "は不明な値です.";
    }

    @Override
    protected V argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return clazz.cast(argument.get(1));
    }

    @Override
    protected String valueToString(V v) {
        return v.name();
    }
}
