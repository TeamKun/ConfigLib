package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Enum2EnumMapValue<K extends Enum<K>, V extends Enum<V>> extends Enum2ObjectMapValue<K, V, Enum2EnumMapValue<K, V>> {
    private final transient Class<V> clazz;
    private transient Predicate<V> filter = x -> true;

    public Enum2EnumMapValue(Class<V> valueClass, K... k) {
        super(new HashMap<>(), k);
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
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(1) + "は不明な値です.";
    }

    @Override
    protected V argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return clazz.cast(argument.get(1));
    }

    @Override
    protected String valueToString(V v) {
        return v.name();
    }
}
