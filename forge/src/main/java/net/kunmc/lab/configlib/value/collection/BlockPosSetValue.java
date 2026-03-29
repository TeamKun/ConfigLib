package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.BlockPosArgument;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockPosSetValue extends SetValue<BlockPos, BlockPosSetValue> {
    public BlockPosSetValue(BlockPos... blockPoses) {
        this(SetUtil.newHashSet(blockPoses));
    }

    public BlockPosSetValue(@NotNull Set<BlockPos> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<Set<BlockPos>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new BlockPosArgument("pos"),
                                                    (pos, ctx) -> Collections.singleton(pos)));
    }

    @Override
    protected List<ArgumentDefinition<Set<BlockPos>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new IntegerArgument("x", opt -> {
            opt.suggestionAction(sb -> {
                value.stream()
                     .map(BlockPos::getX)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), new IntegerArgument("y", opt -> {
            opt.suggestionAction(sb -> {
                int x = (Integer) sb.getParsedArgs()
                                    .get(0);
                value.stream()
                     .filter(l -> l.getX() == x)
                     .map(BlockPos::getY)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), new IntegerArgument("z", opt -> {
            opt.suggestionAction(sb -> {
                int x = (Integer) sb.getParsedArgs()
                                    .get(0);
                int y = (Integer) sb.getParsedArgs()
                                    .get(1);
                value.stream()
                     .filter(l -> l.getX() == x && l.getY() == y)
                     .map(BlockPos::getZ)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), (x, y, z, ctx) -> {
            return value.stream()
                        .filter(p -> p.getX() == x && p.getY() == y && p.getZ() == z)
                        .collect(Collectors.toSet());
        }));
    }

    @Override
    protected String elementToString(BlockPos blockPos) {
        return String.format("x=%d,y=%d,z=%d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
