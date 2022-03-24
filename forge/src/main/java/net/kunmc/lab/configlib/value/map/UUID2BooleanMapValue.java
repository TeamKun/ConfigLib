package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

public class UUID2BooleanMapValue extends UUID2ObjectMapValue<Boolean, UUID2BooleanMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.boolArgument("boolean", null, null);
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
