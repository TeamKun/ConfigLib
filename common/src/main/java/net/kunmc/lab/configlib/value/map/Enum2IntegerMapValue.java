package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2IntegerMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Integer, Enum2IntegerMapValue<T>> {
    public Enum2IntegerMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2IntegerMapValue(Map<T, Integer> value, T... t) {
        super(value, t);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.integerArgument("integer");
    }

    @Override
    protected Integer argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }
}
