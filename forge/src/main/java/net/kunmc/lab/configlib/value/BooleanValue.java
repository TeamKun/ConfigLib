package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;

import java.util.List;

public class BooleanValue extends SingleValue<Boolean, BooleanValue> {
    public BooleanValue(Boolean value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.boolArgument("bool");
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Boolean argumentToValue(List<Object> argument, CommandSource sender) {
        return ((Boolean) argument.get(0));
    }

    @Override
    protected boolean validateOnSet(Boolean newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, Boolean newValue) {
        return newValue + "は不正な値です.";
    }

    @Override
    protected String valueToString(Boolean bool) {
        return bool.toString();
    }

    @Override
    public String toString() {
        return String.format("BooleanValue{value=%b,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
