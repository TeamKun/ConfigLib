package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.IntegerValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class NotSerializedConfig extends BaseConfig {
    public final IntegerValue integerValue = new IntegerValue(1);
   
    public NotSerializedConfig(@NotNull Plugin plugin) {
        super(plugin, false);
    }
}
