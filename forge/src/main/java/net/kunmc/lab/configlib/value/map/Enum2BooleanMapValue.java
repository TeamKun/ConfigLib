package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

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
        builder.boolArgument("bool", null, null);
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected Boolean argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((Boolean) argument.get(1));
    }

    @Override
    protected String valueToString(Boolean bool) {
        return bool.toString();
    }
}
