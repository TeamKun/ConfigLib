package net.kunmc.lab.value;

import org.bukkit.Material;

import java.util.function.Consumer;

public final class MaterialValue implements Value<Material> {
    private Material value;
    private final transient Consumer<Material> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        this(value, x -> {
        });
    }

    public MaterialValue(Material value, Consumer<Material> onSet) {
        this.value = value;
        this.consumer = onSet;
    }

    @Override
    public Material value() {
        return value;
    }

    @Override
    public void value(Material value) {
        this.value = value;
    }

    @Override
    public void onSetValue() {
        consumer.accept(value);
    }

    @Override
    public boolean validate(Material newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public MaterialValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public MaterialValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("MaterialValue{value=%s,listable=%b,writable=%b}", value.toString(), listable, writable);
    }
}
