package net.kunmc.lab.configlib.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.configlib.value.CollectionValue;
import net.kunmc.lab.configlib.value.SingleValue;
import net.kunmc.lab.configlib.value.Value;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseConfig {
    private transient Plugin plugin;
    private transient String entryName;
    private static final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .registerTypeHierarchyAdapter(Team.class, new TeamTypeAdapter())
            .create();

    public static <T extends BaseConfig> T newInstanceFrom(@NotNull File configJSON, @NotNull Class<T> clazz, @NotNull Plugin plugin) {
        String filename = configJSON.getName();
        String json = readJson(configJSON);

        T config = gson.fromJson(json, clazz);
        config.setPlugin(plugin);
        config.setEntryName(filename.substring(0, filename.lastIndexOf('.')));

        return config;
    }

    public BaseConfig(@NotNull Plugin plugin, @NotNull String entryName) {
        setPlugin(plugin);
        this.entryName = entryName;
    }

    protected void setPlugin(Plugin plugin) {
        this.plugin = plugin;
        plugin.getDataFolder().mkdir();
    }

    protected void setEntryName(String entryName) {
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

    public List<Field> getSingleValueFields() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> SingleValue.class.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public List<Field> getCollectionValueFields() {
        return Arrays.stream(getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> CollectionValue.class.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public List<SingleValue<?>> getSingleValues() {
        return getSingleValueFields().stream()
                .map(f -> {
                    try {
                        return f.get(this);
                    } catch (IllegalAccessException exception) {
                        exception.printStackTrace();
                        return null;
                    }
                })
                .map(x -> ((SingleValue<?>) x))
                .collect(Collectors.toList());
    }

    public List<CollectionValue<?, ?>> getCollectionValues() {
        return getCollectionValueFields().stream()
                .map(f -> {
                    try {
                        return f.get(this);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(x -> ((CollectionValue<?, ?>) x))
                .collect(Collectors.toList());
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

    public void saveConfig() {
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

    private void replaceFields(Class<?> clazz, Object src, Object dst) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);

            try {
                replaceField(field, src, dst);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceField(Field field, Object src, Object dst) throws IllegalAccessException {
        if (Value.class.isAssignableFrom(field.getType())) {
            Value srcValue = ((Value) field.get(src));
            Value dstValue = ((Value) field.get(dst));
            replaceFields(field.getType(), srcValue, dstValue);

            Class<?> fieldSuperClass = field.getType().getSuperclass();
            if (!fieldSuperClass.equals(Object.class)) {
                replaceFields(fieldSuperClass, srcValue, dstValue);
            }
        } else {
            Object srcObj = field.get(src);
            field.set(dst, srcObj);
        }
    }
}
