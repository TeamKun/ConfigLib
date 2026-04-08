package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.BlockStateArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockStateSetValue extends SetValue<BlockState, BlockStateSetValue> {
    private transient boolean listOnlyBlockName = false;

    public BlockStateSetValue() {
        this(new HashSet<>());
    }

    public BlockStateSetValue(@NotNull Set<BlockState> value) {
        super(value);
    }

    public BlockStateSetValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected List<ArgumentDefinition<Set<BlockState>>> argumentDefinitionsForAdd() {
        return List.of(new ArgumentDefinition<>(new BlockStateArgument("state"),
                                                (state, ctx) -> Set.of(state.getState())));
    }

    @Override
    protected List<ArgumentDefinition<Set<BlockState>>> argumentDefinitionsForRemove() {
        return List.of(new ArgumentDefinition<>(new BlockStateArgument("state", opt -> {
            opt.suggestionAction(sb -> {
                value().stream()
                       .map(BlockState::toString)
                       .forEach(sb::suggest);
            });
        }), (state, ctx) -> Set.of(state.getState())));
    }

    @Override
    protected String elementToString(BlockState blockState) {
        if (listOnlyBlockName) {
            return blockState.getBlock()
                             .getRegistryName()
                             .getPath();
        } else {
            return blockState.toString();
        }
    }
}
