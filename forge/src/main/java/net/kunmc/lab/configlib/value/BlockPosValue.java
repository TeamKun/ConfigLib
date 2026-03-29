package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.BlockPosArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
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
    protected List<ArgumentDefinition<BlockPos>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new BlockPosArgument("pos"), (pos, ctx) -> pos));
    }

    @Override
    protected String valueToString(BlockPos blockPos) {
        return blockPos.getCoordinatesAsString();
    }
}
