package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.Value;
import net.kunmc.lab.configlib.ValueConfigSchemaEntry;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConfigSchema {
    private final List<ConfigSchemaEntry<?>> entries;

    private ConfigSchema(List<ConfigSchemaEntry<?>> entries) {
        this.entries = List.copyOf(entries);
    }

    public static ConfigSchema fromConfig(CommonBaseConfig config) {
        List<ConfigSchemaEntry<?>> entries = new ArrayList<>();
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(config.getClass())) {
            if (!ConfigUtil.isObservableField(config, field)) {
                continue;
            }
            field.setAccessible(true);
            try {
                if (Value.class.isAssignableFrom(field.getType())) {
                    Value<?, ?> value = (Value<?, ?>) field.get(config);
                    entries.add(ValueConfigSchemaEntry.from(field, value));
                } else if (isNestedPojoType(field.getType())) {
                    collectNestedEntries(config, new Field[]{field}, field.getType(), entries);
                } else {
                    entries.add(PojoConfigSchemaEntry.from(config, new Field[0], field));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return new ConfigSchema(entries);
    }

    private static void collectNestedEntries(Object root,
                                             Field[] parentChain,
                                             Class<?> pojoClass,
                                             List<ConfigSchemaEntry<?>> result) {
        for (Field field : ReflectionUtil.getFieldsIncludingSuperclasses(pojoClass)) {
            if (field.getDeclaringClass() == Object.class) {
                continue;
            }
            if (field.isSynthetic()) {
                continue;
            }
            if (!ConfigUtil.isConfigFieldModifier(field)) {
                continue;
            }
            field.setAccessible(true);
            if (isNestedPojoType(field.getType())) {
                Field[] newChain = appendToChain(parentChain, field);
                collectNestedEntries(root, newChain, field.getType(), result);
            } else {
                result.add(PojoConfigSchemaEntry.from(root, parentChain, field));
            }
        }
    }

    private static Field[] appendToChain(Field[] chain, Field field) {
        Field[] newChain = new Field[chain.length + 1];
        System.arraycopy(chain, 0, newChain, 0, chain.length);
        newChain[chain.length] = field;
        return newChain;
    }

    // Only expand classes explicitly designed as config POJOs (i.e. nested/inner classes).
    // Top-level library types (ConfigStore, Gson, etc.) are treated as opaque leaf values.
    // Enum, primitive wrappers, collections, arrays, and java.* types are leaf values.
    private static boolean isNestedPojoType(Class<?> type) {
        if (type.getEnclosingClass() == null) {
            return false;
        }
        if (type.isEnum()) {
            return false;
        }
        if (type.isAnnotation()) {
            return false;
        }
        if (Value.class.isAssignableFrom(type)) {
            return false;
        }
        return true;
    }

    public List<ConfigSchemaEntry<?>> entries() {
        return List.copyOf(entries);
    }

    public Optional<ConfigSchemaEntry<?>> findEntry(String path) {
        return entries.stream()
                      .filter(e -> e.path()
                                    .asString()
                                    .equals(path))
                      .findFirst();
    }
}
