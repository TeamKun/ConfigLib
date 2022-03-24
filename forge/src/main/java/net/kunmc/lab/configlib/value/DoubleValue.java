package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.NumericValue;
import net.minecraft.command.CommandSource;

import java.util.List;

public class DoubleValue extends NumericValue<Double, DoubleValue> {
    public DoubleValue(Double value) {
        this(value, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public DoubleValue(Double value, Double min, Double max) {
        super(value, min, max);
    }

    @Override
    protected Double copyAdd(Number amount) {
        return value + amount.doubleValue();
    }

    @Override
    protected Double copySub(Number amount) {
        return value - amount.doubleValue();
    }

    @Override
    protected int compareTo(Number n) {
        return value.compareTo(n.doubleValue());
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.doubleArgument("Double", min, max);
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Double argumentToValue(List<Object> argument, CommandSource sender) {
        return ((Double) argument.get(0));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }

    @Override
    public String toString() {
        return String.format("DoubleValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable(), writableByCommand());
    }
}