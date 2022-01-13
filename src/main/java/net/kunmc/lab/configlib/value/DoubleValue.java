package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DoubleValue extends NumericValue<Double> {
    private final Double min;
    private final Double max;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public DoubleValue(Double value) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleValue(Double value, Double min, Double max) {
        super(value);
        this.min = min;
        this.max = max;
    }

    @Override
    protected Double min() {
        return min;
    }

    @Override
    protected Double max() {
        return max;
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public DoubleValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.doubleArgument("Double", min(), max());
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
    protected Double argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Double) argument.get(0));
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public DoubleValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("DoubleValue{value=%f,min=%f,max=%f,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
