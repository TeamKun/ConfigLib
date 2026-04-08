package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.BlockStateArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.block.BlockState;

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
    protected List<ArgumentDefinition<BlockState>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new BlockStateArgument("state"), (state, ctx) -> state.getState()));
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
        return String.format("BlockDataValue{value=%s,modifyEnabled=%b}", value(), isModifyEnabled());
    }
}
