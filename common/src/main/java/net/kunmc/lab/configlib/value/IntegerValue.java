package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.NumericValue;

import java.util.List;

public class IntegerValue extends NumericValue<Integer, IntegerValue> {
    public IntegerValue(Integer value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerValue(Integer value, Integer min, Integer max) {
        super(value, min, max);
    }

    @Override
    public Integer plus(Number other) {
        return value + other.intValue();
    }

    @Override
    public Integer plus(NumericValue<?, ?> other) {
        return value + other.intValue();

    }

    @Override
    public Integer minus(Number other) {
        return value - other.intValue();
    }

    @Override
    public Integer minus(NumericValue<?, ?> other) {
        return value - other.intValue();
    }

    @Override
    public Integer multiply(Number other) {
        return value * other.intValue();
    }

    @Override
    public Integer multiply(NumericValue<?, ?> other) {
        return value * other.intValue();
    }

    @Override
    public Integer divide(Number other) {
        return value / other.intValue();
    }

    @Override
    public Integer divide(NumericValue<?, ?> other) {
        return value / other.intValue();
    }

    @Override
    public Integer mod(Number other) {
        return value % other.intValue();
    }

    @Override
    public Integer mod(NumericValue<?, ?> other) {
        return value % other.intValue();
    }


    @Override
    protected Integer copyAdd(Number amount) {
        return value + amount.intValue();
    }

    @Override
    protected Integer copySub(Number amount) {
        return value - amount.intValue();
    }

    @Override
    protected int compare(Number n) {
        return value.compareTo(n.intValue());
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.integerArgument("Integer", min, max);
    }

    @Override
    protected Integer argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((Integer) argument.get(0));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }

    public String toString() {
        return String.format("IntegerValue{value=%d,min=%d,max=%d,listable=%b,writable=%b}",
                             value,
                             min,
                             max,
                             listable(),
                             writableByCommand());
    }
}
