package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class BlockDataValue extends SingleValue<BlockData> {
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public BlockDataValue(BlockData value) {
        super(value);
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public BlockDataValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("BlockName", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(Material::isBlock)
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "はブロック化出来ない値です.";
    }

    @Override
    protected BlockData argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .map(Material::createBlockData)
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(BlockData newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, BlockData newValue) {
        return newValue.getMaterial().name() + "は不正な値です.";
    }

    @Override
    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value.getMaterial().name() + "に設定しました.";
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public BlockDataValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value.getMaterial().name());
    }

    @Override
    public String toString() {
        return String.format("BlockDataValue{value=%s,listable=%b,writable=%b}", value(), listable, writable);
    }
}
