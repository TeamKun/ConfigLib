package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.IntegerValue;
import net.kunmc.lab.configlib.value.LocationValue;
import net.kunmc.lab.configlib.value.collection.BlockDataSetValue;
import net.kunmc.lab.configlib.value.map.Enum2DoubleMapValue;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(123);
    public final LocationValue locationValue = new LocationValue();
    public final BlockDataSetValue blockDataSetValue = new BlockDataSetValue();
    public final Enum2DoubleMapValue<Material> enum2DoubleMapValue = new Enum2DoubleMapValue<>();
    public final TeamValue teamValue = new TeamValue().onModify(team -> {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage("teamValue modified!");
        });
    });

    public Config(@NotNull Plugin plugin) {
        super(plugin);
    }
}
