package net.kunmc.lab.testplugin;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.value.StringValue;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractConfig extends BaseConfig {
    public final StringValue commonStringValue = new StringValue("");

    public AbstractConfig(@NotNull Plugin plugin) {
        super(plugin);
    }
}
