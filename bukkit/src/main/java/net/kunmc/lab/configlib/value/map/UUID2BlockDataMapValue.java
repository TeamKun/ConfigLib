package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.List;

public class UUID2BlockDataMapValue extends UUID2ObjectMapValue<BlockData, UUID2BlockDataMapValue> {
    private transient boolean listOnlyBlockName = false;

    public UUID2BlockDataMapValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.blockDataArgument("name");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected BlockData argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((BlockData) argument.get(1));
    }

    @Override
    protected String valueToString(BlockData blockData) {
        if (listOnlyBlockName) {
            return blockData.getMaterial().name();
        } else {
            return blockData.getAsString();
        }
    }
}