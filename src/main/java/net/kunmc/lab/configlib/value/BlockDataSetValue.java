package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockDataSetValue extends SetValue<BlockData, BlockDataSetValue> {
    public BlockDataSetValue() {
        this(new HashSet<>());
    }

    public BlockDataSetValue(@NotNull Set<BlockData> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "はブロック化出来ない値です.";
    }

    @Override
    protected Set<BlockData> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(Arrays.stream(Material.values())
                .filter(m -> m.name().equalsIgnoreCase(argument.get(0).toString()))
                .map(Material::createBlockData)
                .findFirst()
                .get());
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value().stream()
                    .map(BlockData::getMaterial)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "はブロック化出来ない値です.";
    }

    @Override
    protected Set<BlockData> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(Arrays.stream(Material.values())
                .filter(m -> m.name().equalsIgnoreCase(argument.get(0).toString()))
                .map(Material::createBlockData)
                .findFirst()
                .get());
    }

    @Override
    protected String elementToString(BlockData blockData) {
        return blockData.getMaterial().name();
    }
}
