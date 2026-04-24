package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.annotation.Masked;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.Objects;

public abstract class ConfigSchemaEntry<E> {
    private final ConfigSchemaPath path;
    private final String entryName;
    private final Field field;
    private final ConfigSchemaMetadata metadata;

    public ConfigSchemaEntry(ConfigSchemaPath path, String entryName, Field field, ConfigSchemaMetadata metadata) {
        this.path = Objects.requireNonNull(path, "path");
        this.entryName = Objects.requireNonNull(entryName, "entryName");
        this.field = Objects.requireNonNull(field, "field");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
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

    public ConfigSchemaMetadata metadata() {
        return metadata;
    }

    public boolean isMasked() {
        return field.isAnnotationPresent(Masked.class);
    }

    public abstract void validate(E value) throws InvalidValueException;

    public abstract E get();

    public abstract E get(CommonBaseConfig config);

    public abstract void set(E value);

    public abstract Object commandObject();

    public abstract boolean supportsModificationCommand();

    public final String displayString() {
        return displayString(get(), DisplayContext.raw());
    }

    public final String displayString(DisplayContext context) {
        return displayString(get(), context);
    }

    public final String displayString(Object fieldValue) {
        return displayString(fieldValue, DisplayContext.raw());
    }

    public final String displayString(Object fieldValue, DisplayContext context) {
        return context.display(displayRawString(fieldValue), this);
    }

    protected abstract String displayRawString(Object fieldValue);

    public abstract int sourceHash();

    public abstract void dispatchModify();
}
