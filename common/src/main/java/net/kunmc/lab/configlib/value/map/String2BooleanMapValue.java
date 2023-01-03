package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2BooleanMapValue extends String2ObjectMapValue<Boolean, String2BooleanMapValue> {
    public String2BooleanMapValue() {
        this(new HashMap<>());
    }

    public String2BooleanMapValue(Map<String, Boolean> value) {
        super(value);
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
    protected String valueToString(Boolean aBoolean) {
        return aBoolean.toString();
    }
}
