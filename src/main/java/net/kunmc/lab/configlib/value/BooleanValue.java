package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Consumer;

public final class BooleanValue implements SingleValue<Boolean> {
    private Boolean value;
    private final transient Consumer<Boolean> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public BooleanValue(Boolean value) {
        this(value, x -> {
        });
    }

    public BooleanValue(Boolean value, Consumer<Boolean> onSet) {
        this.value = value;
        this.consumer = onSet;
    }

    @Override
    public Boolean value() {
        return value;
    }

    @Override
    public void value(Boolean value) {
        this.value = value;
    }

    @Override
    @Internal
    public void onSetValue(Boolean newValue) {
        consumer.accept(newValue);
    }

    @Override
    @Internal
    public boolean validateOnSet(Boolean newValue) {
        return true;
    }

    @Override
    @Internal
    public boolean listable() {
        return listable;
    }

    @Override
    @Internal
    public void appendArgument(UsageBuilder builder) {
        builder.booleanArgument("BooleanArgument", sb -> {
            sb.suggest("true").suggest("false");
        });
    }

    @Override
    @Internal
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    @Internal
    public Boolean argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Boolean) argument.get(0));
    }

    public BooleanValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    @Internal
    public boolean writableByCommand() {
        return writable;
    }

    public BooleanValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("BooleanValue{value=%b,listable=%b,writable=%b}", value, listable, writable);
    }
}
