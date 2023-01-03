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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class BaseConfig extends CommonBaseConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
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
    private final transient boolean makeConfigFile;
    private final transient List<Runnable> onInitializeListeners = new ArrayList<>();
    private transient WatchService watchService;
    private transient TimerTask watchTask;

    public BaseConfig(@NotNull String modId, @NotNull Type type) {
        this(modId, type, true);
    }

    public BaseConfig(@NotNull String modId, @NotNull Type type, boolean makeConfigFile) {
        this.modId = modId;
        this.type = type;
        this.makeConfigFile = makeConfigFile;

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            init();
        } else {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent e) {
        if (type.isCorrectSide()) {
            init();
        }
    }

    private void init() {
        if (!makeConfigFile) {
            return;
        }

        getConfigFolder().mkdirs();
        new Thread() {
            public void run() {
                saveConfigIfAbsent();
                loadConfig();
                onInitializeListeners.forEach(Runnable::run);
            }
        }.start();

        try {
            watchService = FileSystems.getDefault()
                                      .newWatchService();
            WatchKey watchKey = type.getConfigFolder(modId)
                                    .toPath()
                                    .register(watchService, ENTRY_MODIFY);
            watchTask = new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path filePath = getConfigFolder().toPath()
                                                         .resolve((Path) e.context());
                        if (filePath.equals(getConfigFile().toPath())) {
                            loadConfig();
                        }
                    }

                    watchKey.reset();
                }
            };
            new Timer().scheduleAtFixedRate(watchTask, 0, 500);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppedEvent e) {
        if (watchTask != null) {
            watchTask.cancel();
        }
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    /**
     * set listener fired on initialization.
     */
    protected final void onInitialize(Runnable onLoad) {
        onInitializeListeners.add(onLoad);
    }

    private File getConfigFolder() {
        return type.getConfigFolder(modId);
    }

    @Override
    protected Gson gson() {
        return gson;
    }

    @Override
    public File getConfigFile() {
        return new File(getConfigFolder(), entryName() + ".json");
    }

    public enum Type {
        COMMON(modId -> {
            return new File("config/" + modId);
        }, () -> true),
        CLIENT(modId -> {
            return new File("config/" + modId);
        }, () -> {
            return FMLEnvironment.dist.isClient();
        }),
        SERVER(modId -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server.isDedicatedServer()) {
                return new File("world/serverconfig/" + modId);
            } else {
                return new File("saves/" + server.getServerConfiguration()
                                                 .getWorldName() + "/serverconfig/" + modId);
            }
        }, () -> {
            return true;
        });

        private final Function<String, File> getConfigFolder;
        private final Supplier<Boolean> isCorrectSide;

        Type(Function<String, File> getConfigFolder, Supplier<Boolean> isCorrectSide) {
            this.getConfigFolder = getConfigFolder;
            this.isCorrectSide = isCorrectSide;
        }

        File getConfigFolder(String modId) {
            return getConfigFolder.apply(modId);
        }

        boolean isCorrectSide() {
            return isCorrectSide.get();
        }
    }
}
