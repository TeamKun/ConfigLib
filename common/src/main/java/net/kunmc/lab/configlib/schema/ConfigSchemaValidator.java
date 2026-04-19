package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.util.Objects;

@FunctionalInterface
public interface ConfigSchemaValidator<E> {
    void validate(E value) throws InvalidValueException;

    default ConfigSchemaValidator<E> and(ConfigSchemaValidator<E> other) {
        Objects.requireNonNull(other);
        return v -> {
            validate(v);
            other.validate(v);
        };
    }

    static <E> ConfigSchemaValidator<E> noOp() {
        return v -> {
        };
    }
}
