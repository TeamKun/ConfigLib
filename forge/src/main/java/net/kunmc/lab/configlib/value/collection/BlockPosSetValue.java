package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class BlockPosSetValue extends SetValue<BlockPos, BlockPosSetValue> {
    public BlockPosSetValue(BlockPos... blockPoses) {
        this(Sets.newHashSet(blockPoses));
    }

    public BlockPosSetValue(@NotNull Set<BlockPos> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {

    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return false;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return null;
    }

    @Override
    protected Set<BlockPos> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return null;
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {

    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return false;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return null;
    }

    @Override
    protected Set<BlockPos> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return null;
    }

    @Override
    protected String elementToString(BlockPos blockPos) {
        return null;
    }
}
