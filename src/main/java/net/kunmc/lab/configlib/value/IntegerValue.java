package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;

import java.util.function.Consumer;

public final class IntegerValue implements NumericValue<Integer> {
    private Integer value;
    private final Integer min;
    private final Integer max;
    private final transient Consumer<Integer> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public IntegerValue(Integer value) {
        this(value, x -> {
        });
    }

    public IntegerValue(Integer value, Consumer<Integer> onSet) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE, onSet);
    }

    public IntegerValue(Integer value, Integer min, Integer max) {
        this(value, min, max, x -> {
        });
    }

    public IntegerValue(Integer value, Integer min, Integer max, Consumer<Integer> onSet) {
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
        builder.integerArgument("IntegerArgument");
    }

    @Override
    public boolean isCorrectArgument(Object argument) {
        return argument instanceof Integer;
    }

    @Override
    public Integer argumentToValue(Object argument) {
        return ((Integer) argument);
    }

    public IntegerValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public IntegerValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public void value(Integer value) {
        this.value = value;
    }

    @Override
    public void onSetValue(Integer newValue) {
        consumer.accept(newValue);
    }

    @Override
    public Integer min() {
        return min;
    }

    @Override
    public Integer max() {
        return max;
    }

    public String toString() {
        return String.format("IntegerValue{value=%d,min=%d,max=%d,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
