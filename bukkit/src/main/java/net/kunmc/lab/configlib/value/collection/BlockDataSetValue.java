package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.util.SetUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockDataSetValue extends SetValue<BlockData, BlockDataSetValue> {
    private transient boolean listOnlyBlockName = false;

    public BlockDataSetValue() {
        this(new HashSet<>());
    }

    public BlockDataSetValue(@NotNull Set<BlockData> value) {
        super(value);
    }

    public BlockDataSetValue listOnlyBlockName(boolean listOnlyBlockName) {
        this.listOnlyBlockName = listOnlyBlockName;
        return this;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.blockDataArgument("name");
    }

    @Override
    protected Set<BlockData> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return SetUtil.newHashSet(((BlockData) argument.get(0)));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.PHRASE, sb -> {
            value().stream()
                   .map(BlockData::getAsString)
                   .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        try {
            Bukkit.createBlockData(argument.get(0)
                                           .toString());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return argument.get(0) + "はブロック化出来ない値です.";
    }

    @Override
    protected Set<BlockData> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return SetUtil.newHashSet(Bukkit.createBlockData(argument.get(0)
                                                                 .toString()));
    }

    @Override
    protected String elementToString(BlockData blockData) {
        if (listOnlyBlockName) {
            return blockData.getMaterial()
                            .name();
        } else {
            return blockData.getAsString();
        }
    }
}
