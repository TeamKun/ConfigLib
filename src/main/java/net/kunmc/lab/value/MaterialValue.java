package net.kunmc.lab.value;

import org.bukkit.Material;

import java.util.function.Consumer;

public final class MaterialValue implements SingleValue<Material> {
    private String materialName;
    private final transient Consumer<Material> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        this(value, x -> {
        });
    }

    public MaterialValue(Material value, Consumer<Material> onSet) {
        this.materialName = value.name();
        this.consumer = onSet;
    }

    @Override
    public Material value() {
        return Material.valueOf(materialName);
    }

    @Override
    public void value(Material value) {
        this.materialName = value.name();
    }

    @Override
    public void onSetValue(Material newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validateOnSet(Material newValue) {
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
        return String.format("MaterialValue{value=%s,listable=%b,writable=%b}", value().toString(), listable, writable);
    }
}
