package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import org.bukkit.command.CommandSender;

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
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected String argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((String) argument.get(1));
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }
}
