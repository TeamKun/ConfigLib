package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.NumericValue;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FloatValue extends NumericValue<Float> {
    private final Float min;
    private final Float max;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public FloatValue(Float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public FloatValue(Float value, Float min, Float max) {
        super(value);
        this.min = min;
        this.max = max;
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
    protected void add(Number amount) {
        value += amount.floatValue();
    }

    @Override
    protected void sub(Number amount) {
        value -= amount.floatValue();
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
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Float argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Float) argument.get(0));
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
