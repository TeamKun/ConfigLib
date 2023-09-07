package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.SetUtil;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BlockPosSetValue extends SetValue<BlockPos, BlockPosSetValue> {
    public BlockPosSetValue(BlockPos... blockPoses) {
        this(SetUtil.newHashSet(blockPoses));
    }

    public BlockPosSetValue(@NotNull Set<BlockPos> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.blockPosArgument("pos");
    }

    @Override
    protected Set<BlockPos> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return Collections.singleton((BlockPos) argument.get(0));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.integerArgument("x", sb -> {
                   value.stream()
                        .map(BlockPos::getX)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               })
               .integerArgument("y", sb -> {
                   double x = ((double) sb.getParsedArgs()
                                          .get(0));
                   value.stream()
                        .filter(l -> l.getX() == x)
                        .map(BlockPos::getY)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               })
               .integerArgument("z", sb -> {
                   double x = ((double) sb.getParsedArgs()
                                          .get(0));
                   double y = ((double) sb.getParsedArgs()
                                          .get(1));
                   value.stream()
                        .filter(l -> l.getX() == x && l.getY() == y)
                        .map(BlockPos::getZ)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               });
    }

    @Override
    protected Set<BlockPos> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Collections.singleton(((BlockPos) argument.get(0)));
    }

    @Override
    protected String elementToString(BlockPos blockPos) {
        return String.format("x=%d,y=%d,z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
