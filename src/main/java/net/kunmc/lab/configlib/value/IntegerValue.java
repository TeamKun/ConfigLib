package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

public class IntegerValue extends NumericValue<Integer> {
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
        super(value);
        this.min = min;
        this.max = max;
        this.consumer = onSet;
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
    protected void onSetValue(Integer newValue) {
        consumer.accept(newValue);
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
