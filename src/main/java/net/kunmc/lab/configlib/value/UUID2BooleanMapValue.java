package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2BooleanMapValue extends UUID2ObjectMapValue<Boolean> {
    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.booleanArgument("boolean", null, null);
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