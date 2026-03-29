package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.BlockDataArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
import org.bukkit.block.data.BlockData;

import java.util.List;

public class BlockDataValue extends SingleValue<BlockData, BlockDataValue> {
    private transient boolean listOnlyBlockName = false;

    public BlockDataValue(BlockData value) {
        super(value);
    }

    public BlockDataValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected List<ArgumentDefinition<BlockData>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new BlockDataArgument("data"), (data, ctx) -> data));
    }

    @Override
    protected String valueToString(BlockData blockData) {
        if (listOnlyBlockName) {
            return blockData.getMaterial()
                            .name();
        } else {
            return blockData.getAsString();
        }
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}",
                             value(),
                             listable(),
                             writableByCommand());
    }
}
