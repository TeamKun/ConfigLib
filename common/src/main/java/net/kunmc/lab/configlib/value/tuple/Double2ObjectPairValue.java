package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.List;

public abstract class Double2ObjectPairValue<R, T extends Double2ObjectPairValue<R, T>> extends PairValue<Double, R, T> {
    private transient double min = -Double.MAX_VALUE;
    private transient double max = Double.MAX_VALUE;

    public Double2ObjectPairValue(Double left, R right) {
        super(left, right);
    }

    public T leftMin(double min) {
        this.min = min;
        return ((T) this);
    }

    public T leftMax(double max) {
        this.max = max;
        return ((T) this);
    }

    @Override
    protected void appendLeftArgument(ArgumentBuilder builder) {
        builder.doubleArgument("double", min, max);
    }

    @Override
    protected Double argumentToLeftValue(List<Object> argument, CommandContext ctx) {
        return ((Double) argument.get(0));
    }

    @Override
    protected String leftToString(Double left) {
        return left.toString();
    }
}
