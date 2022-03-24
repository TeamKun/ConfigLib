package net.kunmc.lab.samplemod;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;


@Mod(SampleMod.MOD_ID)
public class SampleMod {
    public static final String MOD_ID = "samplemod";
    public static Config config;

    public SampleMod() {
        config = new Config(MOD_ID, BaseConfig.Type.COMMON);
        config.saveConfigIfAbsent();

        ConfigCommand configCommand = new ConfigCommandBuilder(config).build();

        CommandLib.register(new TestCommand(configCommand));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        config.loadConfig();
    }
}
