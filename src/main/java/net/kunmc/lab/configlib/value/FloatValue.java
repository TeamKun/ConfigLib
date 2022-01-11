package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.command.CommandSender;

import java.util.List;
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
    @Internal
    public boolean listable() {
        return listable;
    }

    @Override
    @Internal
    public void appendArgument(UsageBuilder builder) {
        builder.floatArgument("FloatArgument", min(), max());
    }

    @Override
    @Internal
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    @Internal
    public Float argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(0));
    }

    public FloatValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    @Internal
    public boolean writableByCommand() {
        return writable;
    }

    public FloatValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    @Internal
    public Float min() {
        return min;
    }

    @Override
    @Internal
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
    @Internal
    public void onSetValue(Float newValue) {
        consumer.accept(newValue);
    }

    @Override
    public String toString() {
        return String.format("FloatValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
