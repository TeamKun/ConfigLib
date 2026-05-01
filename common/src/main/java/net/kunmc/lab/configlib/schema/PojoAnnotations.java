package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.ConfigCommandDescriptions;
import net.kunmc.lab.configlib.annotation.ConfigNullable;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Range;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

final class PojoAnnotations {
    private PojoAnnotations() {
    }

    @Nullable
    static String descriptionOf(Field field) {
        Description description = field.getAnnotation(Description.class);
        return description == null ? null : description.value();
    }

    static ConfigSchemaValidator<Object> validatorOf(Field field) {
        ConfigSchemaValidator<Object> validator = ConfigSchemaValidator.noOp();
        if (!field.isAnnotationPresent(ConfigNullable.class)) {
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
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_NOT_NULL,
                                                                                                      field.getName())),
                                            field.getName() + " must not be null.");
        }
    }

    private static void validateRange(Field field, Range range, Object value) throws InvalidValueException {
        if (value == null) {
            return;
        }
        if (!(value instanceof Number)) {
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_RANGE_NON_NUMERIC,
                                                                                                      field.getName())),
                                            "@Range can only be used on numeric fields: " + field.getName());
        }

        double numericValue = ((Number) value).doubleValue();
        if (numericValue < range.min() || numericValue > range.max()) {
            String min = formatRangeBound(range.min());
            String max = formatRangeBound(range.max());
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_RANGE,
                                                                                                      field.getName(),
                                                                                                      min,
                                                                                                      max)),
                                            field.getName() + " must be between " + min + " and " + max + ".");
        }
    }

    private static String formatRangeBound(double value) {
        if (value == Math.rint(value) && value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }
}
