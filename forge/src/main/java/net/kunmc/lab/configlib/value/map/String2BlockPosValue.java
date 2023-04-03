package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2BlockPosValue extends String2ObjectMapValue<BlockPos, String2BlockPosValue> {
    public String2BlockPosValue() {
        this(new HashMap<>());
    }

    public String2BlockPosValue(Map<String, BlockPos> value) {
        super(value);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.blockPosArgument("pos");
    }

    @Override
    protected BlockPos argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((BlockPos) argument.get(1));
    }

    @Override
    protected String valueToString(BlockPos blockPos) {
        return String.format("{x:%d, y:%d, z:%d}", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
