package net.kunmc.lab.configlib.util;

import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.*;
import net.kunmc.lab.configlib.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConfigUtil {
    private static final Logger logger = Logger.getLogger(ConfigUtil.class.getName());

    private ConfigUtil() {
    }

    public static String configHeader(CommonBaseConfig config) {
        String name = config.entryName();
        int totalDashes = Math.max(4, 46 - name.length());
        int left = totalDashes / 2;
        int right = totalDashes - left;
        return ChatColorUtil.GRAY + "-".repeat(left) + "[ " + ChatColorUtil.GOLD + ChatColorUtil.BOLD + name + ChatColorUtil.GRAY + " ]" + "-".repeat(
                right);
    }

    public static void replaceFields(CommonBaseConfig config, Object src, Object dst) {
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())) {
            if (!ConfigUtil.isObservableField(config, field)) {
                continue;
            }
            if (!field.trySetAccessible()) {
                logger.fine("Skipping inaccessible field: " + field.getDeclaringClass()
                                                                   .getName() + "#" + field.getName());
                continue;
            }

            try {
                replaceField(config, field, src, dst);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void replaceField(CommonBaseConfig config,
                                     Field field,
                                     Object src,
                                     Object dst) throws IllegalAccessException {
        try {
            Object srcObj = field.get(src);
            Object dstObj = field.get(dst);

            // jsonにキーが存在しない場合nullになる
            if (srcObj == null && field.isAnnotationPresent(Nullable.class)) {
                field.set(dst, null);
                return;
            }

            if (Value.class.isAssignableFrom(field.getType())) {
                replaceValueFields(config, field.getType(), srcObj, dstObj);
            } else {
                field.set(dst, srcObj);
            }
        } catch (NullPointerException ignored) {
            // 新しいフィールドが追加されるとNullPointerExceptionが発生するため握りつぶしている
        }
    }

    private static void replaceValueFields(CommonBaseConfig config, Class<?> clazz, Object src, Object dst) {
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(clazz)) {
            if (!isConfigFieldModifier(field)) {
                continue;
            }
            if (!field.trySetAccessible()) {
                logger.fine("Skipping inaccessible field: " + field.getDeclaringClass()
                                                                   .getName() + "#" + field.getName());
                continue;
            }

            try {
                Object srcObj = field.get(src);
                if (srcObj == null) {
                    if (shouldKeepValueMetadata(field)) {
                        continue;
                    }
                    if (isValuePayloadField(field)) {
                        field.set(dst, null);
                    }
                    continue;
                }
                field.set(dst, srcObj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean shouldKeepValueMetadata(Field field) {
        return field.getName()
                    .equals("description");
    }

    private static boolean isValuePayloadField(Field field) {
        return field.getName()
                    .equals("value");
    }

    public static List<Field> getObservedFields(CommonBaseConfig config, Class<?> targetClass) {
        List<Field> fields = ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())
                                           .stream()
                                           .filter(f -> isObservableField(config, f))
                                           .filter(f -> Object.class.equals(targetClass) || targetClass.isAssignableFrom(
                                                   f.getType()))
                                           .collect(Collectors.toList());
        fields.forEach(x -> x.setAccessible(true));
        return fields;
    }

    public static boolean isObservableField(CommonBaseConfig config, Field field) {
        return field.getDeclaringClass() != CommonBaseConfig.class && field.getDeclaringClass()
                                                                           .isAssignableFrom(config.getClass()) && isConfigFieldModifier(
                field);
    }

    public static boolean isConfigFieldModifier(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
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
}
