package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class IntegerValue extends NumericValue<Integer> {
    private final Integer min;
    private final Integer max;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public IntegerValue(Integer value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerValue(Integer value, Integer min, Integer max) {
        super(value);
        this.min = min;
        this.max = max;
    }
   
    @Override
    protected Integer min() {
        return min;
    }

    @Override
    protected Integer max() {
        return max;
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public IntegerValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.integerArgument("Integer", min(), max());
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
    protected Integer argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Integer) argument.get(0));
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public IntegerValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    public String toString() {
        return String.format("IntegerValue{value=%d,min=%d,max=%d,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
