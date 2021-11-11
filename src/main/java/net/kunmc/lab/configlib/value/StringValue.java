package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;

import java.util.function.Consumer;

public class StringValue implements SingleValue<String> {
    private String value;
    private int min;
    private final int max;
    private transient final Consumer<String> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

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

    @Override
    public String value() {
        return value;
    }

    @Override
    public void value(String value) {
        this.value = value;
    }

    @Override
    public void onSetValue(String newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validateOnSet(String newValue) {
        return newValue.length() >= min && newValue.length() <= max;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    public boolean isCorrectArgument(Object argument) {
        return argument instanceof String;
    }

    @Override
    public String argumentToValue(Object argument) {
        return argument.toString();
    }

    public StringValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public StringValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public String invalidValueMessage(String entryName, String argument) {
        return entryName + "は" + min + "以上" + max + "以下の文字数で入力してください";
    }

    @Override
    public String toString() {
        return String.format("StringValue{value=%s,min=%d,max=%d,listable=%b,writable=%b}", value, min, max, listable, writable);
    }
}
