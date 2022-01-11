package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class BlockDataValue implements SingleValue<BlockData> {
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
    public void onSetValue(BlockData newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validateOnSet(BlockData newValue) {
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
        ctx.success(entryName + ": " + value.getMaterial().name());
    }

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("BlockName", sb -> {
            Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    public BlockData argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .map(Material::createBlockData)
                .findFirst()
                .get();
    }

    @Override
    public String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value.getMaterial().name() + "に設定しました.";
    }
}
