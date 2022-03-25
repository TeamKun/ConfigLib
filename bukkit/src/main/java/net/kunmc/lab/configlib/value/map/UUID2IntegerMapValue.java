package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2IntegerMapValue extends UUID2ObjectMapValue<Integer, UUID2IntegerMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.integerArgument("integer");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSender sender) {
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
