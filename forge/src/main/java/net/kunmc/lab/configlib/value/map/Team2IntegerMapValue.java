package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

public class Team2IntegerMapValue extends Team2ObjectMapValue<Integer, Team2IntegerMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.integerArgument("integer");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSource sender) {
        return "";
    }

    @Override
    protected Integer argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }
}
