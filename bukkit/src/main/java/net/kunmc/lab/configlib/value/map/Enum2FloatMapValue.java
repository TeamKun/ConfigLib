package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2FloatMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Float, Enum2FloatMapValue<T>> {
    public Enum2FloatMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2FloatMapValue(Map<T, Float> value, T... t) {
        super(value, t);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
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
