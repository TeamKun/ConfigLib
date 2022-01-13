package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

public class BooleanValue extends SingleValue<Boolean> {
    private final transient Consumer<Boolean> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public BooleanValue(Boolean value) {
        this(value, x -> {
        });
    }

    public BooleanValue(Boolean value, Consumer<Boolean> onSet) {
        super(value);
        this.consumer = onSet;
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public BooleanValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
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
    protected boolean listable() {
        return listable;
    }

    public BooleanValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("BooleanValue{value=%b,listable=%b,writable=%b}", value, listable, writable);
    }
}
