package net.kunmc.lab.configlib.value.map;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Team2FloatMapValue extends Team2ObjectMapValue<Float, Team2FloatMapValue> {
    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.floatArgument("float", -Float.MAX_VALUE, Float.MAX_VALUE);
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
    protected Float argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(1));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
