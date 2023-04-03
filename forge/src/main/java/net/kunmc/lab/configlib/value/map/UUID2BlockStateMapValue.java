package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateInput;

import java.util.List;

public class UUID2BlockStateMapValue extends UUID2ObjectMapValue<BlockState, UUID2BlockStateMapValue> {
    private transient boolean listOnlyBlockName = false;

    public UUID2BlockStateMapValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.blockStateArgument("name");
    }

    @Override
    protected BlockState argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((BlockStateInput) argument.get(1)).getState();
    }

    @Override
    protected String valueToString(BlockState blockState) {
        if (listOnlyBlockName) {
            return blockState.getBlock()
                             .getRegistryName()
                             .getPath();
        } else {
            return blockState.toString();
        }
    }
}
