package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

public class Integer2IntegerPairValue extends Integer2ObjectPairValue<Integer, Integer2IntegerPairValue> {
    public Integer2IntegerPairValue(Integer left, Integer right) {
        super(left, right);
    }

    @Override
    protected void appendRightArgument(ArgumentBuilder builder) {
        builder.integerArgument("integer2");
    }

    @Override
    protected boolean isCorrectRightArgument(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectRightArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected Integer argumentToRightValue(List<Object> argument, CommandSource sender) {
        return ((Integer) argument.get(1));
    }

    @Override
    protected String rightToString(Integer right) {
        return right.toString();
    }
}
