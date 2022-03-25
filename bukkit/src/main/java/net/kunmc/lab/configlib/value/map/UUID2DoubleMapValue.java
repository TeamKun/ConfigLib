package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2DoubleMapValue extends UUID2ObjectMapValue<Double, UUID2DoubleMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.doubleArgument("double");
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
    protected Double argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Double) argument.get(1));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }
}
