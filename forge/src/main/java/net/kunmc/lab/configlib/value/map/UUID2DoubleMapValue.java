package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public class UUID2DoubleMapValue extends UUID2ObjectMapValue<Double, UUID2DoubleMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.doubleArgument("double", -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Override
    protected Double argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((Double) argument.get(1));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }
}
