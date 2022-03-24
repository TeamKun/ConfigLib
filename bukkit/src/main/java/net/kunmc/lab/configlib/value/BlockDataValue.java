package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BlockDataValue extends SingleValue<BlockData, BlockDataValue> {
    private transient boolean listOnlyBlockName = false;

    public BlockDataValue(BlockData value) {
        super(value);
    }

    public BlockDataValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.blockDataArgument("name");
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return "";
    }

    @Override
    protected BlockData argumentToValue(List<Object> argument, CommandSender sender) {
        return ((BlockData) argument.get(0));
    }

    @Override
    protected boolean validateOnSet(BlockData newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, BlockData newValue) {
        return "";
    }

    @Override
    protected String valueToString(BlockData blockData) {
        if (listOnlyBlockName) {
            return blockData.getMaterial().name();
        } else {
            return blockData.getAsString();
        }
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}", value(), listable(), writableByCommand());
    }
}