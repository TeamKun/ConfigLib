package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
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
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected Set<BlockState> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return Sets.newHashSet(((BlockStateInput) argument.get(0)).getState());
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
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected Set<BlockState> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        return Sets.newHashSet(((BlockStateInput) argument.get(0)).getState());
    }

    @Override
    protected String elementToString(BlockState blockState) {
        if (listOnlyBlockName) {
            return blockState.getBlock().getRegistryName().getPath();
        } else {
            return blockState.toString();
        }
    }
}
