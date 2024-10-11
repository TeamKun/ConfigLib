package net.kunmc.lab.configlib.util;

import net.kunmc.lab.configlib.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static List<Field> getObservedFields(CommonBaseConfig config, Class<?> targetClass) {
        List<Field> fields = ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())
                                           .stream()
                                           .filter(f -> !Modifier.isStatic(f.getModifiers()))
                                           .filter(f -> !Modifier.isTransient(f.getModifiers()))
                                           .filter(f -> targetClass.isAssignableFrom(f.getType()))
                                           .collect(Collectors.toList());
        fields.forEach(x -> x.setAccessible(true));
        return fields;
    }

    public static List<Field> getValueFields(CommonBaseConfig config) {
        return getObservedFields(config, Value.class);
    }

    public static List<Field> getSingleValueFields(CommonBaseConfig config) {
        return getObservedFields(config, SingleValue.class);
    }

    public static List<Field> getCollectionValueFields(CommonBaseConfig config) {
        return getObservedFields(config, CollectionValue.class);
    }

    public static List<Field> getMapValueFields(CommonBaseConfig config) {
        return getObservedFields(config, MapValue.class);
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
                                     .map(x -> ((Value<?, ?>) x))
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

    private ConfigUtil() {
    }
}
