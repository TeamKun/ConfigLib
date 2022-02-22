package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class Team2BlockDataMapValue extends Team2ObjectMapValue<BlockData, Team2BlockDataMapValue> {
    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equalsIgnoreCase(argument.get(1).toString()));
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return argument.get(1) + "はブロック化出来ない値です.";
    }

    @Override
    protected BlockData argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equalsIgnoreCase(argument.get(1).toString()))
                .map(Material::createBlockData)
                .findFirst()
                .get();
    }

    @Override
    protected String valueToString(BlockData blockData) {
        return blockData.getMaterial().name();
    }
}
