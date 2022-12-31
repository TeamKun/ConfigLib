package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2FloatMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Float, Enum2FloatMapValue<T>> {
    public Enum2FloatMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2FloatMapValue(Map<T, Float> value, T... t) {
        super(value, t);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.floatArgument("float", -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSource sender) {
        return "";
    }

    @Override
    protected Float argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((Float) argument.get(1));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
