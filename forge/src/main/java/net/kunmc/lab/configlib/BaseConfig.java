package net.kunmc.lab.configlib;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.gson.BlockPosTypeAdapter;
import net.kunmc.lab.configlib.gson.BlockStateTypeAdapter;
import net.kunmc.lab.configlib.gson.LocationTypeAdapter;
import net.kunmc.lab.configlib.gson.ScorePlayerTeamAdapter;
import net.minecraft.block.BlockState;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.codehaus.plexus.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public abstract class BaseConfig {
    private final transient String modId;
    private final transient Type type;
    private transient String entryName = "";
    protected transient boolean enableGet = true;
    protected transient boolean enableList = true;
    protected transient boolean enableModify = true;
    protected transient boolean enableReload = true;
    private static final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .registerTypeHierarchyAdapter(BlockPos.class, new BlockPosTypeAdapter())
            .registerTypeHierarchyAdapter(BlockState.class, new BlockStateTypeAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeHierarchyAdapter(ScorePlayerTeam.class, new ScorePlayerTeamAdapter())
            .create();

    public static <T extends BaseConfig> T newInstanceFrom(@NotNull String modId, @NotNull Type type, @NotNull File configJSON, @NotNull Class<T> clazz) {
        String filename = configJSON.getName();
        String json = readJson(configJSON);

        T config = null;
        try {
            config = clazz.getConstructor(String.class, Type.class).newInstance(modId, type);
            config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));

            config.replaceFields(clazz, gson.fromJson(json, clazz), config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return config;
    }

    public BaseConfig(@NotNull String modId, @NotNull Type type) {
        this.modId = modId;
        this.type = type;
        type.getConfigFolder(modId).mkdirs();

        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = type.getConfigFolder(modId).toPath().register(watcher, ENTRY_MODIFY);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    for (WatchEvent<?> e : watchKey.pollEvents()) {
                        Path filePath = type.getConfigFolder(modId).toPath().resolve((Path) e.context());
                        if (filePath.equals(getConfigFile().toPath())) {
                            loadConfig();
                        }
                    }

                    watchKey.reset();
                }
            };
            new Timer().scheduleAtFixedRate(task, 0, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public File getConfigFile() {
        return new File(type.getConfigFolder(modId), entryName() + ".json");
    }

    public String entryName() {
        if (entryName.equals("")) {
            String n = getClass().getSimpleName();
            return n.substring(0, 1).toLowerCase() + n.substring(1);
        } else {
            return entryName;
        }
    }

    private static String readJson(File jsonFile) {
        String json = null;
        try {
            json = Files.readLines(jsonFile, StandardCharsets.UTF_8).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    private static void writeJson(File jsonFile, String json) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(jsonFile), StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void saveConfig() {
        try {
            getConfigFile().createNewFile();
            writeJson(getConfigFile(), gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfigIfAbsent() {
        if (!getConfigFile().exists()) {
            saveConfig();
        }
    }

    public void saveConfigIfPresent() {
        if (getConfigFile().exists()) {
            saveConfig();
        }
    }

    public boolean loadConfig() {
        if (!getConfigFile().exists()) {
            return false;
        }

        BaseConfig config = gson.fromJson(readJson(getConfigFile()), this.getClass());
        replaceFields(this.getClass(), config, this);

        return true;
    }

    void replaceFields(Class<?> clazz, Object src, Object dst) {
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
                e.printStackTrace();
            }
        }
    }

    private void replaceField(Field field, Object src, Object dst) throws IllegalAccessException {
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
        }),
        CLIENT(modId -> {
            return new File("config/" + modId);
        }),
        SERVER(modId -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server.isDedicatedServer()) {
                return new File("world/serverconfig/" + modId);
            } else {
                return new File("saves/" + server.getServerConfiguration().getWorldName() + "/serverconfig/" + modId);
            }
        });

        private final Function<String, File> getConfigFolder;

        Type(Function<String, File> getConfigFolder) {
            this.getConfigFolder = getConfigFolder;
        }

        File getConfigFolder(String modId) {
            return getConfigFolder.apply(modId);
        }
    }
}
