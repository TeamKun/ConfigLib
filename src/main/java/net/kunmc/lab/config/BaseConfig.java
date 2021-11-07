package net.kunmc.lab.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.value.Value;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public abstract class BaseConfig {
    private final transient Plugin plugin;
    public final transient File configJSON;
    private static final transient Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

    public BaseConfig(Plugin plugin) {
        this.plugin = plugin;
        this.configJSON = new File(plugin.getDataFolder(), this.getClass().getSimpleName() + ".json");
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

    public void loadConfig() throws FileNotFoundException {
        if (!configJSON.exists()) {
            throw new FileNotFoundException(configJSON.getName() + " not found");
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
        } else {
            Object srcObj = field.get(src);
            field.set(dst, srcObj);
        }
    }
}
