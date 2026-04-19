package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ConfigSchemaEntry<E> {
    private final ConfigSchemaPath path;
    private final String entryName;
    private final Field field;
    private final Object source;
    private final ConfigSchemaMetadata metadata;
    private final ConfigSchemaValidator<E> validator;
    private final ConfigSchemaAccessor<E> accessor;

    public ConfigSchemaEntry(ConfigSchemaPath path,
                             String entryName,
                             Field field,
                             Object source,
                             ConfigSchemaMetadata metadata,
                             ConfigSchemaValidator<E> validator,
                             ConfigSchemaAccessor<E> accessor) {
        this.path = Objects.requireNonNull(path, "path");
        this.entryName = Objects.requireNonNull(entryName, "entryName");
        this.field = Objects.requireNonNull(field, "field");
        this.source = Objects.requireNonNull(source, "source");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.validator = Objects.requireNonNull(validator, "validator");
        this.accessor = Objects.requireNonNull(accessor, "accessor");
    }

    public ConfigSchemaPath path() {
        return path;
    }

    public String entryName() {
        return entryName;
    }

    public Field field() {
        return field;
    }

    public Object source() {
        return source;
    }

    public ConfigSchemaMetadata metadata() {
        return metadata;
    }

    public void validate(E value) throws InvalidValueException {
        validator.validate(value);
    }

    public E get() {
        return accessor.get();
    }

    public void set(E value) {
        accessor.set(value);
    }
}
