package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public class UUID2BooleanMapValue extends UUID2ObjectMapValue<Boolean, UUID2BooleanMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.boolArgument("bool");
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
