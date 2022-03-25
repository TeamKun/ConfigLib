package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Team2FloatMapValue extends Team2ObjectMapValue<Float, Team2FloatMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.floatArgument("float");
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
    protected Float argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(1));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
