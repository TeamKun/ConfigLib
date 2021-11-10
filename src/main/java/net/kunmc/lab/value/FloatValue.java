package net.kunmc.lab.value;

import dev.kotx.flylib.command.UsageBuilder;

import java.util.function.Consumer;

public final class FloatValue implements NumericValue<Float> {
    private Float value;
    private final Float min;
    private final Float max;
    private final transient Consumer<Float> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public FloatValue(Float value) {
        this(value, x -> {
        });
    }

    public FloatValue(Float value, Consumer<Float> onSet) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE, onSet);
    }

    public FloatValue(Float value, Float min, Float max) {
        this(value, min, max, x -> {
        });
    }

    public FloatValue(Float value, Float min, Float max, Consumer<Float> onSet) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.consumer = onSet;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.floatArgument("FloatArgument");
    }

    @Override
    public boolean isCorrectArgument(Object argument) {
        return argument instanceof Float;
    }

    @Override
    public Float argumentToValue(Object argument) {
        return ((Float) argument);
    }

    public FloatValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public FloatValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public Float min() {
        return min;
    }

    @Override
    public Float max() {
        return max;
    }

    @Override
    public Float value() {
        return value;
    }

    @Override
    public void value(Float value) {
        this.value = value;
    }

    @Override
    public void onSetValue(Float newValue) {
        consumer.accept(newValue);
    }

    @Override
    public String toString() {
        return String.format("FloatValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
