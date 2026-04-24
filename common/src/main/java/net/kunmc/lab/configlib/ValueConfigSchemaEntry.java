package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.schema.ConfigSchemaMetadata;
import net.kunmc.lab.configlib.schema.ConfigSchemaPath;

import java.lang.reflect.Field;

public final class ValueConfigSchemaEntry<E> extends ConfigSchemaEntry<E> {
    private final Value<E, ?> value;

    private ValueConfigSchemaEntry(ConfigSchemaPath path,
                                   String entryName,
                                   Field field,
                                   Value<E, ?> value,
                                   ConfigSchemaMetadata metadata) {
        super(path, entryName, field, metadata);
        this.value = value;
    }

    public static <E> ValueConfigSchemaEntry<E> from(Field field, Value<E, ?> value) {
        String fieldName = field.getName();
        return new ValueConfigSchemaEntry<>(new ConfigSchemaPath(fieldName),
                                            value.resolveEntryName(fieldName),
                                            field,
                                            value,
                                            new ConfigSchemaMetadata(value.description()));
    }

    @Override
    public void validate(E value) throws InvalidValueException {
        this.value.validate(value);
    }

    @Override
    public E get() {
        return value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(CommonBaseConfig config) {
        try {
            Object fieldValue = field().get(config);
            if (fieldValue == null) {
                return null;
            }
            return ((Value<E, ?>) fieldValue).value();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(E value) {
        this.value.value(value);
    }

    @Override
    public Object commandObject() {
        return value;
    }

    @Override
    public boolean supportsModificationCommand() {
        if (value instanceof SingleValue) {
            return ((SingleValue<?, ?>) value).isModifyEnabled();
        }
        if (value instanceof CollectionValue) {
            CollectionValue<?, ?, ?> collectionValue = (CollectionValue<?, ?, ?>) value;
            return collectionValue.isAddEnabled() || collectionValue.isRemoveEnabled() || collectionValue.isClearEnabled();
        }
        if (value instanceof MapValue) {
            MapValue<?, ?, ?> mapValue = (MapValue<?, ?, ?>) value;
            return mapValue.isPutEnabled() || mapValue.isRemoveEnabled() || mapValue.isClearEnabled();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String displayRawString(Object fieldValue) {
        if (fieldValue instanceof Value) {
            return ((Value<?, ?>) fieldValue).displayString();
        }
        return ((Value<Object, ?>) value).displayString(fieldValue);
    }

    @Override
    public int sourceHash() {
        return value.valueHashCode();
    }

    @Override
    public void dispatchModify() {
        value.dispatchModify(value.value());
    }
}
