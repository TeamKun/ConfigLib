package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Team2DoubleMapValue extends Team2ObjectMapValue<Double, Team2DoubleMapValue> {
    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.doubleArgument("double", -Double.MAX_VALUE, Double.MAX_VALUE);
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
    protected Double argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Double) argument.get(1));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }
}
