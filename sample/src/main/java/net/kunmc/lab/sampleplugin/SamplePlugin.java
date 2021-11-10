package net.kunmc.lab.sampleplugin;

import dev.kotx.flylib.FlyLib;
import net.kunmc.lab.command.ConfigCommand;
import net.kunmc.lab.command.ConfigCommandBuilder;
import net.kunmc.lab.config.BaseConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class SamplePlugin extends JavaPlugin {
    public void onEnable() {
        BaseConfig config = new Config(this, "config");
        config.saveConfigIfAbsent();
        config.loadConfig();

        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();

        FlyLib.create(this, builder -> {
            builder.command(new MainCommand("sample", configCommand));
        });
    }
}