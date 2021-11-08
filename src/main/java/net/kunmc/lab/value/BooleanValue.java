package net.kunmc.lab.value;

import java.util.function.Consumer;

public final class BooleanValue implements Value<Boolean> {
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
    public void onSetValue(Boolean newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validate(Boolean newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public BooleanValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
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
