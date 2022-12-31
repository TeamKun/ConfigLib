package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2BooleanMapValue extends UUID2ObjectMapValue<Boolean, UUID2BooleanMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.boolArgument("bool");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSender sender) {
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
