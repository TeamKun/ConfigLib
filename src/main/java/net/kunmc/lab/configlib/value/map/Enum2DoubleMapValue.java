package net.kunmc.lab.configlib.value.map;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2DoubleMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Double, Enum2DoubleMapValue<T>> {
    public Enum2DoubleMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2DoubleMapValue(Map<T, Double> value, T... t) {
        super(value, t);
    }

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
