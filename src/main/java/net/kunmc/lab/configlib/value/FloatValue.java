package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

public class FloatValue extends NumericValue<Float> {
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
        super(value);
        this.min = min;
        this.max = max;
        this.consumer = onSet;
    }

    @Override
    protected Float min() {
        return min;
    }

    @Override
    protected Float max() {
        return max;
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public FloatValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.floatArgument("Float", min(), max());
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected Float argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(0));
    }

    @Override
    protected void onSetValue(Float newValue) {
        consumer.accept(newValue);
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public FloatValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("FloatValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
