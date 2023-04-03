package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.NumericValue;

import java.util.List;

public class FloatValue extends NumericValue<Float, FloatValue> {
    public FloatValue(Float value) {
        this(value, -Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public FloatValue(Float value, Float min, Float max) {
        super(value, min, max);
    }

    @Override
    public Float plus(Number other) {
        return value + other.floatValue();
    }

    @Override
    public Float plus(NumericValue<?, ?> other) {
        return value + other.floatValue();

    }

    @Override
    public Float minus(Number other) {
        return value - other.floatValue();
    }

    @Override
    public Float minus(NumericValue<?, ?> other) {
        return value - other.floatValue();
    }

    @Override
    public Float multiply(Number other) {
        return value * other.floatValue();
    }

    @Override
    public Float multiply(NumericValue<?, ?> other) {
        return value * other.floatValue();
    }

    @Override
    public Float divide(Number other) {
        return value / other.floatValue();
    }

    @Override
    public Float divide(NumericValue<?, ?> other) {
        return value / other.floatValue();
    }

    @Override
    public Float mod(Number other) {
        return value % other.floatValue();
    }

    @Override
    public Float mod(NumericValue<?, ?> other) {
        return value % other.floatValue();
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
    protected int compare(Number n) {
        return value.compareTo(n.floatValue());
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.floatArgument("float", min, max);
    }

    @Override
    protected Float argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((Float) argument.get(0));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }

    @Override
    public String toString() {
        return String.format("FloatValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}",
                             value,
                             min,
                             max,
                             listable(),
                             writableByCommand());
    }
}
