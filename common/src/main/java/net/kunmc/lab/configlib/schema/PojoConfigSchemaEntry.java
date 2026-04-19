package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Nullable;
import net.kunmc.lab.configlib.annotation.Range;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.Objects;

public final class PojoConfigSchemaEntry<E> extends ConfigSchemaEntry<E> {
    private final Object config;
    private final ConfigSchemaValidator<E> validator;

    private PojoConfigSchemaEntry(ConfigSchemaPath path,
                                  String entryName,
                                  Field field,
                                  Object config,
                                  ConfigSchemaMetadata metadata,
                                  ConfigSchemaValidator<E> validator) {
        super(path, entryName, field, metadata);
        this.config = Objects.requireNonNull(config, "config");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    public static PojoConfigSchemaEntry<Object> from(CommonBaseConfig config, Field field) {
        field.setAccessible(true);
        String fieldName = field.getName();
        return new PojoConfigSchemaEntry<>(new ConfigSchemaPath(fieldName),
                                           fieldName,
                                           field,
                                           config,
                                           new ConfigSchemaMetadata(descriptionOf(field)),
                                           validatorOf(field));
    }

    private static String descriptionOf(Field field) {
        Description description = field.getAnnotation(Description.class);
        return description == null ? null : description.value();
    }

    private static ConfigSchemaValidator<Object> validatorOf(Field field) {
        ConfigSchemaValidator<Object> validator = ConfigSchemaValidator.noOp();
        if (!field.isAnnotationPresent(Nullable.class)) {
            validator = validator.and(value -> validateNotNull(field, value));
        }
        Range range = field.getAnnotation(Range.class);
        if (range != null) {
            validator = validator.and(value -> validateRange(field, range, value));
        }
        return validator;
    }

    private static void validateNotNull(Field field, Object value) throws InvalidValueException {
        if (value == null) {
            throw new InvalidValueException(field.getName() + " must not be null.");
        }
    }

    private static void validateRange(Field field, Range range, Object value) throws InvalidValueException {
        if (value == null) {
            return;
        }
        if (!(value instanceof Number)) {
            throw new InvalidValueException("@Range can only be used on numeric fields: " + field.getName());
        }

        double numericValue = ((Number) value).doubleValue();
        if (numericValue < range.min() || numericValue > range.max()) {
            throw new InvalidValueException(field.getName() + " must be between " + formatRangeBound(range.min()) + " and " + formatRangeBound(
                    range.max()) + ".");
        }
    }

    private static String formatRangeBound(double value) {
        if (value == Math.rint(value) && value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public void validate(E value) throws InvalidValueException {
        validator.validate(value);
    }

    @Override
    public E get() {
        return get((CommonBaseConfig) config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(CommonBaseConfig config) {
        try {
            return (E) field().get(config);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(E value) {
        try {
            field().set(config, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object commandObject() {
        return get();
    }

    @Override
    public boolean supportsModificationCommand() {
        return false;
    }

    @Override
    public String displayString() {
        return String.valueOf(get());
    }

    @Override
    public String displayString(Object fieldValue) {
        return String.valueOf(fieldValue);
    }

    @Override
    public int sourceHash() {
        return Objects.hashCode(get());
    }

    @Override
    public void dispatchModify() {
    }
}
