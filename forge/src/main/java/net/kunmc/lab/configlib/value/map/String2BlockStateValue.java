package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2BlockStateValue extends String2ObjectMapValue<BlockState, String2BlockStateValue> {
    private transient boolean listOnlyBlockName = false;

    public String2BlockStateValue() {
        this(new HashMap<>());
    }

    public String2BlockStateValue(Map<String, BlockState> value) {
        super(value);
    }

    public String2BlockStateValue listOnlyBlockName(boolean listOnlyBlockName) {
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
