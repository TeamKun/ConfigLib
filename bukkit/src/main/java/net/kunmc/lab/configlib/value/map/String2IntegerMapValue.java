package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2IntegerMapValue extends String2ObjectMapValue<Integer, String2IntegerMapValue> {
    public String2IntegerMapValue() {
        this(new HashMap<>());
    }

    public String2IntegerMapValue(Map<String, Integer> value) {
        super(value);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.integerArgument("integer");
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
    protected Integer argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }
}
