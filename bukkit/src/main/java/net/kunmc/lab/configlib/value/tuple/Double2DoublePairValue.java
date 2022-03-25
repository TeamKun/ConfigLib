package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Double2DoublePairValue extends Double2ObjectPairValue<Double, Double2DoublePairValue> {
    private transient double min = -Double.MAX_VALUE;
    private transient double max = Double.MAX_VALUE;

    public Double2DoublePairValue(Double left, Double right) {
        super(left, right);
    }

    public Double2DoublePairValue rightMin(double min) {
        this.min = min;
        return this;
    }

    public Double2DoublePairValue rightMax(double max) {
        this.max = max;
        return this;
    }

    @Override
    protected void appendRightArgument(ArgumentBuilder builder) {
        builder.doubleArgument("double2");
    }

    @Override
    protected boolean isCorrectRightArgument(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectRightArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    @Override
    protected Double argumentToRightValue(List<Object> argument, CommandSender sender) {
        return ((Double) argument.get(1));
    }

    @Override
    protected String rightToString(Double right) {
        return right.toString();
    }
}
