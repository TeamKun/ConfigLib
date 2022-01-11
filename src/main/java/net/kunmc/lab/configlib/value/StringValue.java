package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.SuggestionAction;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.TextArgument;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class StringValue implements SingleValue<String> {
    private String value;
    private int min;
    private final int max;
    private transient final Consumer<String> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;
    protected transient String name = "StringValue";
    protected transient TextArgument.Type type = TextArgument.Type.WORD;
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
        this.value = value;
        this.min = min;
        this.max = max;
        this.consumer = onSet;
    }

    public StringValue name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public StringValue type(@NotNull TextArgument.Type type) {
        this.type = type;
        return this;
    }

    public StringValue suggestionAction(@NotNull SuggestionAction action) {
        this.suggestionAction = action;
        return this;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public void value(String value) {
        this.value = value;
    }

    @Override
    @Internal
    public void onSetValue(String newValue) {
        consumer.accept(newValue);
    }

    @Override
    @Internal
    public boolean validateOnSet(String newValue) {
        return newValue.length() >= min && newValue.length() <= max;
    }

    @Override
    @Internal
    public boolean listable() {
        return listable;
    }

    @Override
    @Internal
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument(name, type, suggestionAction);
    }

    @Override
    @Internal
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    @Internal
    public String argumentToValue(List<Object> argument, CommandSender sender) {
        return argument.get(0).toString();
    }

    public StringValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    @Internal
    public boolean writableByCommand() {
        return writable;
    }

    public StringValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    @Internal
    public String invalidValueMessage(String entryName, String argument) {
        return entryName + "は" + min + "以上" + max + "以下の文字数で入力してください";
    }

    @Override
    public String toString() {
        return String.format("StringValue{value=%s,min=%d,max=%d,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
