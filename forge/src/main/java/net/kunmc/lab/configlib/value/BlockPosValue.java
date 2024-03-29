package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlockPosValue extends SingleValue<BlockPos, BlockPosValue> {
    public BlockPosValue() {
        this(null);
    }

    public BlockPosValue(BlockPos value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.blockPosArgument("pos");
    }

    @Override
    protected BlockPos argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((BlockPos) argument.get(0));
    }

    @Override
    protected String valueToString(BlockPos blockPos) {
        return blockPos.getCoordinatesAsString();
    }
}
