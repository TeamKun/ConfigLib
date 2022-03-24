package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Team2BooleanMapValue extends Team2ObjectMapValue<Boolean, Team2BooleanMapValue> {
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
    protected String valueToString(Boolean bool) {
        return bool.toString();
    }
}
