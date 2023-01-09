package net.kunmc.lab.testplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.IntegerValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class Config2 extends BaseConfig {
    public final IntegerValue n = new IntegerValue(12).onInitialize(x -> TestPlugin.broadcast("n initialize"))
                                                      .onModify(x -> TestPlugin.broadcast(x.toString()));

    public Config2(@NotNull Plugin plugin) {
        super(plugin);
    }
}
