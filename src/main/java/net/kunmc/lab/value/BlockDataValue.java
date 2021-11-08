package net.kunmc.lab.value;

import dev.kotx.flylib.command.CommandContext;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.function.Consumer;

public final class BlockDataValue implements Value<BlockData> {
    private String materialName;
    private final transient Consumer<BlockData> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public BlockDataValue(BlockData value) {
        this(value, x -> {
        });
    }

    public BlockDataValue(BlockData value, Consumer<BlockData> onSet) {
        this.materialName = value.getMaterial().name();
        this.consumer = onSet;
    }

    @Override
    public BlockData value() {
        return Material.valueOf(materialName).createBlockData();
    }

    @Override
    public void value(BlockData value) {
        this.materialName = value.getMaterial().name();
    }

    @Override
    public void onSetValue() {
        consumer.accept(value());
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
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}", value(), listable, writable);
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + materialName);
    }

    @Override
    public String succeedSetMessage(String entryName, BlockData newValue) {
        return entryName + "の値を" + newValue.getMaterial().name() + "に設定しました.";
    }
}
