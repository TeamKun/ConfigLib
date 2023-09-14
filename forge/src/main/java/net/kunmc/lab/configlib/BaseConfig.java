package net.kunmc.lab.configlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.gson.*;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BaseConfig extends CommonBaseConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
                                                      .enableComplexMapKeySerialization()
                                                      .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                                                      .registerTypeHierarchyAdapter(BlockPos.class,
                                                                                    new BlockPosTypeAdapter())
                                                      .registerTypeHierarchyAdapter(BlockState.class,
                                                                                    new BlockStateTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Location.class,
                                                                                    new LocationTypeAdapter())
                                                      .registerTypeHierarchyAdapter(ItemStack.class,
                                                                                    new ItemStackTypeAdapter())
                                                      .registerTypeHierarchyAdapter(ScorePlayerTeam.class,
                                                                                    new ScorePlayerTeamAdapter())
                                                      .registerTypeHierarchyAdapter(Value.class, new ValueTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Nameable.class,
                                                                                    new NameableTypeAdapter())
                                                      .registerTypeHierarchyAdapter(Set.class, new SetTypeAdapter())
                                                      .create();
    private final transient String modId;
    private final transient Type type;
    private final transient Consumer<Option> options;

    public BaseConfig(@NotNull String modId, @NotNull Type type) {
        this(modId, type, option -> {
        });
    }

    public BaseConfig(@NotNull String modId, @NotNull Type type, Consumer<Option> options) {
        this.modId = modId;
        this.type = type;
        this.options = options;

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent e) {
        if (type.isCorrectSide()) {
            init(options, (t, ex) -> {
                ex.printStackTrace();
                e.getServer()
                 .close();
            });
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppedEvent e) {
        if (type.shouldClose) {
            close();
        }
    }

    @Override
    protected File getConfigFolder() {
        return type.getConfigFolder(modId);
    }

    @Override
    protected Gson gson() {
        return GSON;
    }

    public enum Type {
        COMMON(modId -> {
            return new File("config/" + modId);
        }, () -> true, false),
        CLIENT(modId -> {
            return new File("config/" + modId);
        }, () -> {
            return FMLEnvironment.dist.isClient();
        }, false),
        SERVER(modId -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server.isDedicatedServer()) {
                return new File("world/serverconfig/" + modId);
            } else {
                return new File("saves/" + server.getServerConfiguration()
                                                 .getWorldName() + "/serverconfig/" + modId);
            }
        }, () -> true, true);

        private final Function<String, File> getConfigFolder;
        private final Supplier<Boolean> isCorrectSide;
        private final boolean shouldClose;

        Type(Function<String, File> getConfigFolder, Supplier<Boolean> isCorrectSide, boolean shouldClose) {
            this.getConfigFolder = getConfigFolder;
            this.isCorrectSide = isCorrectSide;
            this.shouldClose = shouldClose;
        }

        File getConfigFolder(String modId) {
            return getConfigFolder.apply(modId);
        }

        boolean isCorrectSide() {
            return isCorrectSide.get();
        }
    }
}
