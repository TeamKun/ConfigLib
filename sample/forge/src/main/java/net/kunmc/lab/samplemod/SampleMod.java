package net.kunmc.lab.samplemod;

import net.kunmc.lab.commandlib.CommandLib;
import net.kunmc.lab.configlib.ConfigCommand;
import net.kunmc.lab.configlib.ConfigCommandBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(SampleMod.MOD_ID)
public class SampleMod {
    public static final String MOD_ID = "samplemod";
    public static CommonConfig commonConfig;
    public static ClientConfig clientConfig;
    public static ServerConfig serverConfig;
    public static NotSerializedConfig notSerializedConfig;
    private final ConfigCommandBuilder builder;

    public SampleMod() {
        commonConfig = new CommonConfig(MOD_ID);
        serverConfig = new ServerConfig(MOD_ID);
        notSerializedConfig = new NotSerializedConfig(MOD_ID);
        builder = new ConfigCommandBuilder(commonConfig)
                .addConfig(serverConfig)
                .addConfig(notSerializedConfig);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public void onServerStartingOnClient(FMLServerStartingEvent event) {
        clientConfig = new ClientConfig(MOD_ID);
        builder.addConfig(clientConfig);
    }

    @SubscribeEvent
    public void onServerStartingOnDedicated(FMLServerStartingEvent event) {
        ConfigCommand configCommand = builder.build();
        CommandLib.register(new TestCommand(configCommand));
    }
}
