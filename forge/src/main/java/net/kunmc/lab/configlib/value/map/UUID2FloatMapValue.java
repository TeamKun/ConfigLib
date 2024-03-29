package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public class UUID2FloatMapValue extends UUID2ObjectMapValue<Float, UUID2FloatMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.floatArgument("float", -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    @Override
    protected Float argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((Float) argument.get(1));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
