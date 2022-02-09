package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BooleanValue extends SingleValue<Boolean, BooleanValue> {
    public BooleanValue(Boolean value) {
        super(value);
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.booleanArgument("Boolean", sb -> {
                    sb.suggest("true").suggest("false");
                },
                null);
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
    protected Boolean argumentToValue(List<Object> argument, CommandSender sender) {
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
    public String toString() {
        return String.format("BooleanValue{value=%b,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
