package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2BooleanMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Boolean, Enum2BooleanMapValue<T>> {
    public Enum2BooleanMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2BooleanMapValue(Map<T, Boolean> value, T... t) {
        super(value, t);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.boolArgument("bool");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected Boolean argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((Boolean) argument.get(1));
    }

    @Override
    protected String valueToString(Boolean bool) {
        return bool.toString();
    }
}
