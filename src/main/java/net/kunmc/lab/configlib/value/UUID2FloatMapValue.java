package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2FloatMapValue extends UUID2ObjectMapValue<Float> {
    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.floatArgument("float");
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
