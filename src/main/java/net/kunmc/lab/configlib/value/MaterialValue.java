package net.kunmc.lab.configlib.value;

import org.bukkit.Material;

public final class MaterialValue extends EnumValue<Material> {
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        super(value);
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public MaterialValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public MaterialValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("MaterialValue{value=%s,listable=%b,writable=%b}", value().toString(), listable, writable);
    }
}
