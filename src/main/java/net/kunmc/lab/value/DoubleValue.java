package net.kunmc.lab.value;

import java.util.function.Consumer;

public final class DoubleValue implements NumericValue<Double> {
    private Double value;
    private final Double min;
    private final Double max;
    private final transient Consumer<Double> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public DoubleValue(Double value) {
        this(value, x -> {
        });
    }

    public DoubleValue(Double value, Consumer<Double> onSet) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE, onSet);
    }

    public DoubleValue(Double value, Double min, Double max) {
        this(value, min, max, x -> {
        });
    }

    public DoubleValue(Double value, Double min, Double max, Consumer<Double> onSet) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.consumer = onSet;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public DoubleValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public DoubleValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public Double min() {
        return min;
    }

    @Override
    public Double max() {
        return max;
    }

    @Override
    public Double value() {
        return value;
    }

    @Override
    public void value(Double value) {
        this.value = value;
    }

    @Override
    public void onSetValue() {
        consumer.accept(value);
    }

    @Override
    public String toString() {
        return String.format("DoubleValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
