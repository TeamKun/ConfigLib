package net.kunmc.lab.configlib.util;

import net.kunmc.lab.configlib.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static List<Field> getValueFields(CommonBaseConfig config) {
        return Arrays.stream(config.getClass()
                                   .getDeclaredFields())
                     .filter(f -> !Modifier.isStatic(f.getModifiers()))
                     .filter(f -> Value.class.isAssignableFrom(f.getType()))
                     .peek(f -> f.setAccessible(true))
                     .collect(Collectors.toList());
    }

    public static List<Field> getSingleValueFields(CommonBaseConfig config) {
        return Arrays.stream(config.getClass()
                                   .getDeclaredFields())
                     .filter(f -> !Modifier.isStatic(f.getModifiers()))
                     .filter(f -> SingleValue.class.isAssignableFrom(f.getType()))
                     .peek(f -> f.setAccessible(true))
                     .collect(Collectors.toList());
    }

    public static List<Field> getCollectionValueFields(CommonBaseConfig config) {
        return Arrays.stream(config.getClass()
                                   .getDeclaredFields())
                     .filter(f -> !Modifier.isStatic(f.getModifiers()))
                     .filter(f -> CollectionValue.class.isAssignableFrom(f.getType()))
                     .peek(f -> f.setAccessible(true))
                     .collect(Collectors.toList());
    }

    public static List<Field> getMapValueFields(CommonBaseConfig config) {
        return Arrays.stream(config.getClass()
                                   .getDeclaredFields())
                     .filter(f -> !Modifier.isStatic(f.getModifiers()))
                     .filter(f -> MapValue.class.isAssignableFrom(f.getType()))
                     .peek(f -> f.setAccessible(true))
                     .collect(Collectors.toList());
    }

    public static List<Value<?, ?>> getValues(CommonBaseConfig config) {
        return getValueFields(config).stream()
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

    public static List<SingleValue<?, ?>> getSingleValues(CommonBaseConfig config) {
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

    public static List<CollectionValue<?, ?, ?>> getCollectionValues(CommonBaseConfig config) {
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

    public static List<MapValue<?, ?, ?>> getMapValues(CommonBaseConfig config) {
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
