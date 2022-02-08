package net.kunmc.lab.configlib;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.configlib.adapter.BlockDataTypeAdapter;
import net.kunmc.lab.configlib.adapter.LocationTypeAdapter;
import net.kunmc.lab.configlib.adapter.MaterialTypeAdapter;
import net.kunmc.lab.configlib.adapter.TeamTypeAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.ReflectionUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseConfig {
    private transient Plugin plugin;
    private transient String entryName = "";
    protected transient boolean enableGet = true;
    protected transient boolean enableList = true;
    protected transient boolean enableModify = true;
    protected transient boolean enableReload = true;
    private static final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .registerTypeHierarchyAdapter(Team.class, new TeamTypeAdapter())
            .registerTypeHierarchyAdapter(BlockData.class, new BlockDataTypeAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeHierarchyAdapter(Material.class, new MaterialTypeAdapter())
            .create();

    public static <T extends BaseConfig> T newInstanceFrom(@NotNull File configJSON, @NotNull Class<T> clazz, @NotNull Plugin plugin) {
        String filename = configJSON.getName();
        String json = readJson(configJSON);

        T config = null;
        try {
            config = clazz.getConstructor(Plugin.class).newInstance(plugin);
            config.setPlugin(plugin);
            config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));

            config.replaceFields(clazz, gson.fromJson(json, clazz), config);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return config;
    }

    public BaseConfig(@NotNull Plugin plugin) {
        setPlugin(plugin);
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

    void setPlugin(@NotNull Plugin plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdir();
    }

    protected void setEntryName(@NotNull String entryName) {
        this.entryName = entryName;
    }

    public File getConfigFile() {
        return new File(plugin.getDataFolder(), entryName() + ".json");
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
            // 新しいValueが追加された時のためにNullPointerExceptionを回避する
        }
    }
}
