package net.kunmc.lab.configlib.util.function;

import net.kunmc.lab.configlib.exception.InvalidValueException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@FunctionalInterface
public interface Validator<T> {
    void validate(T t) throws InvalidValueException;

    default Validator<T> and(@NotNull Validator<T> other) {
        Objects.requireNonNull(other);
        return x -> {
            validate(x);
            other.validate(x);
        };
    }
}
