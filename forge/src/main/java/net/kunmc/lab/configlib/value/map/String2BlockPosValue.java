package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.BlockPosArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
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
    protected List<PutArgumentDefinition<String, BlockPos>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new BlockPosArgument("pos"),
                                                                                (pos, ctx) -> pos)));
    }

    @Override
    protected String valueToString(BlockPos blockPos) {
        return String.format("{x:%d, y:%d, z:%d}", blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
