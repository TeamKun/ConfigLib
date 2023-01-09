package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.LocationValue;
import net.kunmc.lab.configlib.value.TeamValue;
import net.kunmc.lab.configlib.value.collection.BlockDataSetValue;
import net.kunmc.lab.configlib.value.map.Enum2DoubleMapValue;
import net.kunmc.lab.configlib.value.tuple.Integer2IntegerPairValue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(123);
    public final LocationValue locationValue = new LocationValue();
    public final BlockDataSetValue blockDataSetValue = new BlockDataSetValue();
    public final Enum2DoubleMapValue<Material> enum2DoubleMapValue = new Enum2DoubleMapValue<>();
    public final TeamValue teamValue = new TeamValue().onModify(team -> {
        Bukkit.getOnlinePlayers()
              .forEach(p -> {
                  p.sendMessage("teamValue modified!");
              });
    });
    public final Integer2IntegerPairValue integer2IntegerPairValue = new Integer2IntegerPairValue(1, 100).leftMin(1)
                                                                                                         .leftMax(99)
                                                                                                         .rightMin(2)
                                                                                                         .rightMax(100)
                                                                                                         .setValidator(
                                                                                                                 pair -> pair.getLeft() < pair.getRight(),
                                                                                                                 pair -> "first arg must be lower than second arg.");

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}
