package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public abstract class Integer2ObjectPairValue<R, T extends Integer2ObjectPairValue<R, T>> extends PairValue<Integer, R, T> {
    private transient int min = Integer.MIN_VALUE;
    private transient int max = Integer.MAX_VALUE;

    public Integer2ObjectPairValue(Integer left, R right) {
        super(left, right);
    }

    public T leftMin(int min) {
        this.min = min;
        return ((T) this);
    }

    public T leftMax(int max) {
        this.max = max;
        return ((T) this);
    }

    @Override
    protected void appendLeftArgument(ArgumentBuilder builder) {
        builder.integerArgument("integer", min, max);
    }

    @Override
    protected boolean isCorrectLeftArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectLeftArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected Integer argumentToLeftValue(List<Object> argument, CommandContext ctx) {
        return ((Integer) argument.get(0));
    }

    @Override
    protected String leftToString(Integer left) {
        return left.toString();
    }
}
