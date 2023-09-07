package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.SetUtil;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateInput;
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
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.blockStateArgument("name");
    }

    @Override
    protected Set<BlockState> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return SetUtil.newHashSet(((BlockStateInput) argument.get(0)).getState());
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.blockStateArgument("name", sb -> {
            value().stream()
                   .map(BlockState::toString)
                   .forEach(sb::suggest);
        });
    }

    @Override
    protected Set<BlockState> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return SetUtil.newHashSet(((BlockStateInput) argument.get(0)).getState());
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
