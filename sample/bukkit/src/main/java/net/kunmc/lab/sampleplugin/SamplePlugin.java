package net.kunmc.lab.sampleplugin;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class SamplePlugin extends JavaPlugin {
    public static Config config;
    public static NotSerializedConfig notSerializedConfig;

    @Override
    public void onEnable() {
        config = new Config(this);
        notSerializedConfig = new NotSerializedConfig(this);

        ConfigCommand configCommand = new ConfigCommandBuilder(config)
                .addConfig(notSerializedConfig)
                .build();
        CommandLib.register(this, new TestCommand(configCommand));
    }
}
