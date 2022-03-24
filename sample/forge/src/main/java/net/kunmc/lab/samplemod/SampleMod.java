package net.kunmc.lab.samplemod;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SampleMod.MOD_ID)
public class SampleMod {
    public static final String MOD_ID = "samplemod";
    public static CommonConfig commonConfig;
    public static ClientConfig clientConfig;
    public static ServerConfig serverConfig;
    private final ConfigCommandBuilder builder;

    public SampleMod() {
        commonConfig = new CommonConfig(MOD_ID);
        serverConfig = new ServerConfig(MOD_ID);
        builder = new ConfigCommandBuilder(commonConfig).addConfig(serverConfig);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        if (FMLEnvironment.dist.isClient()) {
            clientConfig = new ClientConfig(MOD_ID);
            builder.addConfig(clientConfig);

            clientConfig.saveConfigIfAbsent();
            clientConfig.loadConfig();
        }

        commonConfig.saveConfigIfAbsent();
        commonConfig.loadConfig();

        serverConfig.saveConfigIfAbsent();
        serverConfig.loadConfig();

        ConfigCommand configCommand = builder.build();
        CommandLib.register(new TestCommand(configCommand));
    }
}
