package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public class Integer2IntegerPairValue extends Integer2ObjectPairValue<Integer, Integer2IntegerPairValue> {
    private transient int min = Integer.MIN_VALUE;
    private transient int max = Integer.MAX_VALUE;

    public Integer2IntegerPairValue(Integer left, Integer right) {
        super(left, right);
    }

    public Integer2IntegerPairValue rightMin(int min) {
        this.min = min;
        return this;
    }

    public Integer2IntegerPairValue rightMax(int max) {
        this.max = max;
        return this;
    }

    @Override
    protected void appendRightArgument(ArgumentBuilder builder) {
        builder.integerArgument("integer2", min, max);
    }

    @Override
    protected Integer argumentToRightValue(List<Object> argument, CommandContext ctx) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String rightToString(Integer right) {
        return right.toString();
    }
}
