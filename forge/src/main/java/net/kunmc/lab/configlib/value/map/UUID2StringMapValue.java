package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;

import java.util.List;

public class UUID2StringMapValue extends UUID2ObjectMapValue<String, UUID2StringMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE);
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
    protected String argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((String) argument.get(1));
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }
}
