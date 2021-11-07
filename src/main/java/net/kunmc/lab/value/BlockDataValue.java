package net.kunmc.lab.value;

import org.bukkit.block.data.BlockData;

import java.util.function.Consumer;

public final class BlockDataValue implements Value<BlockData> {
    private BlockData value;
    private final transient Consumer<BlockData> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public BlockDataValue(BlockData value) {
        this(value, x -> {
        });
    }

    public BlockDataValue(BlockData value, Consumer<BlockData> onSet) {
        this.value = value;
        this.consumer = onSet;
    }

    @Override
    public BlockData value() {
        return value;
    }

    @Override
    public void value(BlockData value) {
        this.value = value;
    }

    @Override
    public void onSetValue() {
        consumer.accept(value);
    }

    @Override
    public boolean validate(BlockData newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public BlockDataValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public BlockDataValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}", value.toString(), listable, writable);
    }
}
