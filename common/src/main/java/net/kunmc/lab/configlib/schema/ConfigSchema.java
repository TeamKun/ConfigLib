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
                } else {
                    entries.add(PojoConfigSchemaEntry.from(config, field));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return new ConfigSchema(entries);
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
