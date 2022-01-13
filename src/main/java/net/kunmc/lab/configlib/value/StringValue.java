package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.SuggestionAction;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class StringValue extends SingleValue<String> {
    private final int min;
    private final int max;
    private transient final Consumer<String> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;
    protected transient String name = "String";
    protected transient StringArgument.Type type = StringArgument.Type.WORD;
    protected transient SuggestionAction suggestionAction = builder -> {
    };

    public StringValue(String value) {
        this(value, x -> {
        });
    }

    public StringValue(String value, Consumer<String> onSet) {
        this(value, 0, 256, onSet);
    }

    public StringValue(String value, int min, int max) {
        this(value, min, max, x -> {
        });
    }

    public StringValue(String value, int min, int max, Consumer<String> onSet) {
        super(value);
        this.min = min;
        this.max = max;
        this.consumer = onSet;
    }

    public StringValue name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public StringValue type(@NotNull StringArgument.Type type) {
        this.type = type;
        return this;
    }

    public StringValue suggestionAction(@NotNull SuggestionAction action) {
        this.suggestionAction = action;
        return this;
    }


    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public StringValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument(name, type, suggestionAction);
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String argumentToValue(List<Object> argument, CommandSender sender) {
        return argument.get(0).toString();
    }

    @Override
    protected void onSetValue(String newValue) {
        consumer.accept(newValue);
    }

    @Override
    protected boolean validateOnSet(String newValue) {
        return newValue.length() >= min && newValue.length() <= max;
    }

    @Override
    protected String invalidValueMessage(String entryName, String argument) {
        return entryName + "は" + min + "以上" + max + "以下の文字数で入力してください";
    }

    @Override
    protected boolean listable() {
        return listable;
    }


    public StringValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("StringValue{value=%s,min=%d,max=%d,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
