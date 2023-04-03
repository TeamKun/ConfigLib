package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateInput;

import java.util.List;

public class BlockStateValue extends SingleValue<BlockState, BlockStateValue> {
    private transient boolean listOnlyBlockName = false;

    public BlockStateValue() {
        this(null);
    }

    public BlockStateValue(BlockState value) {
        super(value);
    }

    public BlockStateValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.blockStateArgument("name");
    }

    @Override
    protected BlockState argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((BlockStateInput) argument.get(0)).getState();
    }

    @Override
    protected String valueToString(BlockState blockState) {
        if (value == null) {
            return "null";
        }

        if (listOnlyBlockName) {
            return blockState.getBlock()
                             .getRegistryName()
                             .getPath();
        } else {
            return blockState.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}",
                             value(),
                             listable(),
                             writableByCommand());
    }
}
