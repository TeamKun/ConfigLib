package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

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
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected Integer argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }
}
