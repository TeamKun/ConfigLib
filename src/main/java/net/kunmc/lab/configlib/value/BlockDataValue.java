package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class BlockDataValue extends SingleValue<BlockData, BlockDataValue> {
    public BlockDataValue(BlockData value) {
        super(value);
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("BlockName", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "はブロック化出来ない値です.";
    }

    @Override
    protected BlockData argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .map(Material::createBlockData)
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(BlockData newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, BlockData newValue) {
        return newValue.getMaterial().name() + "は不正な値です.";
    }

    @Override
    protected String valueToString(BlockData blockData) {
        return blockData.getMaterial().name();
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}", value(), listable(), writableByCommand());
    }
}
