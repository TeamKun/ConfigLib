package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.NumericValue;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FloatValue extends NumericValue<Float, FloatValue> {
    public FloatValue(Float value) {
        this(value, -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public FloatValue(Float value, Float min, Float max) {
        super(value, min, max);
    }

    @Override
    protected Float copyAdd(Number amount) {
        return value + amount.floatValue();
    }

    @Override
    protected Float copySub(Number amount) {
        return value - amount.floatValue();
    }

    @Override
    protected int compareTo(Number n) {
        return value.compareTo(n.floatValue());
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.floatArgument("float", min, max);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, CommandSender sender, List<Object> argument) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Float argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(0));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }

    @Override
    public String toString() {
        return String.format("FloatValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable(), writableByCommand());
    }
}
