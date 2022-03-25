package net.kunmc.lab.samplemod;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.LocationValue;
import net.kunmc.lab.configlib.value.TeamValue;
import net.kunmc.lab.configlib.value.collection.BlockStateSetValue;
import net.kunmc.lab.configlib.value.map.Enum2DoubleMapValue;
import net.minecraft.item.ItemTier;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ServerConfig extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(123);
    public final LocationValue locationValue = new LocationValue();
    public final BlockStateSetValue blockStateSetValue = new BlockStateSetValue();
    public final Enum2DoubleMapValue<ItemTier> enum2DoubleMapValue = new Enum2DoubleMapValue<>();
    public final TeamValue teamValue = new TeamValue().onModify(team -> {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(p -> {
            p.sendMessage(new StringTextComponent("teamValue modified!"), UUID.randomUUID());
        });
    });
    public final Integer2IntegerPairValue integer2IntegerPairValue = new Integer2IntegerPairValue(1, 100)
            .leftMin(1).leftMax(99)
            .rightMin(2).rightMax(100)
            .setValidator(pair -> pair.getLeft() < pair.getRight(), pair -> "first arg must be lower than second arg.");
   
    public ServerConfig(@NotNull String modId) {
        super(modId, Type.SERVER);
    }
}
