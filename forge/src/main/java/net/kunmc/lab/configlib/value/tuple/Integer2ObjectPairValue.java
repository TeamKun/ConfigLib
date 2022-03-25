package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

public abstract class Integer2ObjectPairValue<R, T extends Integer2ObjectPairValue<R, T>> extends PairValue<Integer, R, T> {
    public Integer2ObjectPairValue(Integer left, R right) {
        super(left, right);
    }

    @Override
    protected void appendLeftArgument(ArgumentBuilder builder) {
        builder.integerArgument("integer");
    }

    @Override
    protected boolean isCorrectLeftArgument(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectLeftArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected Integer argumentToLeftValue(List<Object> argument, CommandSource sender) {
        return ((Integer) argument.get(0));
    }

    @Override
    protected String leftToString(Integer left) {
        return left.toString();
    }
}
