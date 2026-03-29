package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.BlockStateArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.block.BlockState;

import java.util.List;
import java.util.UUID;

public class UUID2BlockStateMapValue extends UUID2ObjectMapValue<BlockState, UUID2BlockStateMapValue> {
    private transient boolean listOnlyBlockName = false;

    public UUID2BlockStateMapValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected List<PutArgumentDefinition<UUID, BlockState>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new BlockStateArgument("state"),
                                                                                (state, ctx) -> state.getState())));
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
