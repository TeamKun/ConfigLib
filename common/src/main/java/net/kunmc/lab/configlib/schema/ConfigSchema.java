package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.Value;
import net.kunmc.lab.configlib.util.ConfigUtil;

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
        List<Field> fields = ConfigUtil.getValueFields(config);
        List<ConfigSchemaEntry<?>> entries = new ArrayList<>(fields.size());
        for (Field field : fields) {
            try {
                Value<?, ?> value = (Value<?, ?>) field.get(config);
                entries.add(entryFromValue(field, value));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return new ConfigSchema(entries);
    }

    private static <E> ConfigSchemaEntry<E> entryFromValue(Field field, Value<E, ?> value) {
        String fieldName = field.getName();
        ConfigSchemaPath path = new ConfigSchemaPath(fieldName);
        String entryName = value.resolveEntryName(fieldName);
        ConfigSchemaMetadata metadata = new ConfigSchemaMetadata(value.description());
        ConfigSchemaValidator<E> validator = value::validate;
        ConfigSchemaAccessor<E> accessor = new ConfigSchemaAccessor<>() {
            @Override
            public E get() {
                return value.value();
            }

            @Override
            public void set(E v) {
                value.value(v);
            }
        };
        return new ConfigSchemaEntry<>(path, entryName, field, value, metadata, validator, accessor);
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
