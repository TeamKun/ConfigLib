package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.BlockStateInput;

import java.util.List;

public class Team2BlockStateMapValue extends Team2ObjectMapValue<BlockState, Team2BlockStateMapValue> {
    private transient boolean listOnlyBlockName = false;

    public Team2BlockStateMapValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.blockStateArgument("name");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSource sender) {
        return "";
    }

    @Override
    protected BlockState argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((BlockStateInput) argument.get(1)).getState();
    }

    @Override
    protected String valueToString(BlockState blockState) {
        if (listOnlyBlockName) {
            return blockState.getBlockState()
                             .getBlock()
                             .getRegistryName()
                             .getPath();
        } else {
            return blockState.toString();
        }
    }
}
