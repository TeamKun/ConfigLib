package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;
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

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.doubleArgument("DoubleArgument", min(), max());
    }

    @Override
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    public Double argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Double) argument.get(0));
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
    public void onSetValue(Double newValue) {
        consumer.accept(newValue);
    }

    @Override
    public String toString() {
        return String.format("DoubleValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
