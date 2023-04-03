package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.StringArgument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2StringMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, String, Enum2StringMapValue<T>> {
    public Enum2StringMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2StringMapValue(Map<T, String> value, T... t) {
        super(value, t);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE);
    }

    @Override
    protected String argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((String) argument.get(1));
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }
}
