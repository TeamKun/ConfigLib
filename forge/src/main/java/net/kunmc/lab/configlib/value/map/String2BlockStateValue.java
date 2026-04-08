package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.BlockStateArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.minecraft.block.BlockState;

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
    protected List<PutArgumentDefinition<String, BlockState>> argumentDefinitionsForPut() {
        return List.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
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
