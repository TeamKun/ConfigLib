package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

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
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected Boolean argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Boolean) argument.get(1));
    }

    @Override
    protected String valueToString(Boolean aBoolean) {
        return aBoolean.toString();
    }
}
