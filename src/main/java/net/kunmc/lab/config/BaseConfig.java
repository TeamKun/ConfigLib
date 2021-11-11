package net.kunmc.lab.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.value.CollectionValue;
import net.kunmc.lab.value.SingleValue;
import net.kunmc.lab.value.Value;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseConfig {
    private final transient Plugin plugin;
    public final transient File configJSON;
    private final transient String entryName;
    private static final transient Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();

    public BaseConfig(@NotNull Plugin plugin, @NotNull String entryName) {
        this.plugin = plugin;
        this.entryName = entryName;

        plugin.getDataFolder().mkdir();
        this.configJSON = new File(plugin.getDataFolder(), entryName() + ".json");
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

    public void saveConfig() {
        try {
            configJSON.createNewFile();
            String json = gson.toJson(this);
            try (FileWriter writer = new FileWriter(configJSON)) {
                writer.write(json);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfigIfAbsent() {
        if (!configJSON.exists()) {
            saveConfig();
        }
    }

    public void saveConfigIfPresent() {
        if (configJSON.exists()) {
            saveConfig();
        }
    }

    public boolean loadConfig() {
        if (!configJSON.exists()) {
            return false;
        }

        String json = null;
        try {
            json = Files.readLines(configJSON, Charset.defaultCharset()).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseConfig config = gson.fromJson(json, this.getClass());
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
