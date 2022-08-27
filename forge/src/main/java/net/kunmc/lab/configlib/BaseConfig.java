package net.kunmc.lab.configlib;

import com.google.common.io.Files;
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
import org.codehaus.plexus.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class BaseConfig {
    private final transient String modId;
    private final transient Type type;
    private final transient boolean makeConfigFile;
    private transient String entryName = "";
    protected transient boolean enableGet = true;
    protected transient boolean enableList = true;
    protected transient boolean enableModify = true;
    protected transient boolean enableReload = true;
    private final transient List<Runnable> onLoadListeners = new ArrayList<>();
    private transient WatchService watchService;
    private transient TimerTask watchTask;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .registerTypeHierarchyAdapter(BlockPos.class, new BlockPosTypeAdapter())
            .registerTypeHierarchyAdapter(BlockState.class, new BlockStateTypeAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter())
            .registerTypeHierarchyAdapter(ScorePlayerTeam.class, new ScorePlayerTeamAdapter())
            .registerTypeHierarchyAdapter(Value.class, new ValueTypeAdapter())
            .registerTypeHierarchyAdapter(Nameable.class, new NameableTypeAdapter())
            .registerTypeHierarchyAdapter(Set.class, new SetTypeAdapter())
            .create();

    public static <T extends BaseConfig> T newInstanceFrom(@NotNull File configJSON, @NotNull Constructor<T> constructor, Object... arguments) {
        String filename = configJSON.getName();
        String json = readJson(configJSON);
        Class<T> clazz = constructor.getDeclaringClass();

        try {
            T config = constructor.newInstance(arguments);
            config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));
            replaceFields(clazz, gson.fromJson(json, clazz), config);
            return config;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

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
                onLoadListeners.forEach(Runnable::run);
            }
        }.start();

        try {
            watchService = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = type.getConfigFolder(modId).toPath().register(watchService, ENTRY_MODIFY);
            watchTask = new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path filePath = getConfigFolder().toPath().resolve((Path) e.context());
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
    protected final void onLoad(Runnable onLoad) {
        onLoadListeners.add(onLoad);
    }

    boolean isGetEnabled() {
        return enableGet;
    }

    boolean isListEnabled() {
        return enableList;
    }

    boolean isModifyEnabled() {
        return enableModify;
    }

    boolean isReloadEnabled() {
        return enableReload;
    }

    protected void setEntryName(@NotNull String entryName) {
        this.entryName = entryName;
    }

    private File getConfigFolder() {
        return type.getConfigFolder(modId);
    }

    public File getConfigFile() {
        return new File(getConfigFolder(), entryName() + ".json");
    }

    public String entryName() {
        if (entryName.equals("")) {
            String n = getClass().getSimpleName();
            return n.substring(0, 1).toLowerCase() + n.substring(1);
        } else {
            return entryName;
        }
    }

    protected void saveConfig() {
        if (!type.isCorrectSide()) {
            return;
        }

        try {
            getConfigFile().createNewFile();
            writeJson(getConfigFile(), gson.toJson(this));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void saveConfigIfAbsent() {
        if (!getConfigFile().exists()) {
            saveConfig();
        }
    }

    protected void saveConfigIfPresent() {
        if (getConfigFile().exists()) {
            saveConfig();
        }
    }

    protected boolean loadConfig() {
        if (!getConfigFile().exists()) {
            return false;
        }

        BaseConfig config = gson.fromJson(readJson(getConfigFile()), this.getClass());
        replaceFields(this.getClass(), config, this);
        return true;
    }

    private static String readJson(File jsonFile) {
        try {
            return Files.readLines(jsonFile, StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeJson(File jsonFile, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void replaceFields(Class<?> clazz, Object src, Object dst) {
        for (Field field : ReflectionUtils.getFieldsIncludingSuperclasses(clazz)) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
            } catch (Exception e) {
                // InaccessibleObjectExceptionが発生した場合スルーする
                continue;
            }

            try {
                replaceField(field, src, dst);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void replaceField(Field field, Object src, Object dst) throws IllegalAccessException {
        try {
            List<Field> fieldList = ReflectionUtils.getFieldsIncludingSuperclasses(field.getType());
            Object srcObj = field.get(src);
            Object dstObj = field.get(dst);

            if (fieldList.isEmpty()) {
                field.set(dst, srcObj);
            } else {
                replaceFields(field.getType(), srcObj, dstObj);
            }
        } catch (NullPointerException ignored) {
            // 新しいフィールドが追加されるとNullPointerExceptionが発生するため握りつぶしている
        }
    }

    public enum Type {
        COMMON(modId -> {
            return new File("config/" + modId);
        }, () -> {
            return true;
        }),
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
                return new File("saves/" + server.getServerConfiguration().getWorldName() + "/serverconfig/" + modId);
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
