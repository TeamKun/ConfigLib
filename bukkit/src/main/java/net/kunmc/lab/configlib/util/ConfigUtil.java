package net.kunmc.lab.configlib.util;

import net.kunmc.lab.configlib.BaseConfig;
import net.kunmc.lab.configlib.CollectionValue;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.SingleValue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static List<Field> getSingleValueFields(BaseConfig config) {
        return Arrays.stream(config.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> SingleValue.class.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public static List<Field> getCollectionValueFields(BaseConfig config) {
        return Arrays.stream(config.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> CollectionValue.class.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public static List<Field> getMapValueFields(BaseConfig config) {
        return Arrays.stream(config.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> MapValue.class.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public static List<SingleValue<?, ?>> getSingleValues(BaseConfig config) {
        return getSingleValueFields(config).stream()
                .map(f -> {
                    try {
                        return f.get(config);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(x -> ((SingleValue<?, ?>) x))
                .collect(Collectors.toList());
    }

    public static List<CollectionValue<?, ?, ?>> getCollectionValues(BaseConfig config) {
        return getCollectionValueFields(config).stream()
                .map(f -> {
                    try {
                        return f.get(config);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(x -> ((CollectionValue<?, ?, ?>) x))
                .collect(Collectors.toList());
    }

    public static List<MapValue<?, ?, ?>> getMapValues(BaseConfig config) {
        return getMapValueFields(config).stream()
                .map(f -> {
                    try {
                        return f.get(config);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(x -> ((MapValue<?, ?, ?>) x))
                .collect(Collectors.toList());
    }
}
