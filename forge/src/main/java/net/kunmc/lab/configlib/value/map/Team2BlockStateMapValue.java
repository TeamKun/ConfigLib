package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.BlockStateArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.minecraft.block.BlockState;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.List;

public class Team2BlockStateMapValue extends Team2ObjectMapValue<BlockState, Team2BlockStateMapValue> {
    private transient boolean listOnlyBlockName = false;

    public Team2BlockStateMapValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected List<PutArgumentDefinition<ScorePlayerTeam, BlockState>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new BlockStateArgument("state"),
                                                                                (state, ctx) -> state.getState())));
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
