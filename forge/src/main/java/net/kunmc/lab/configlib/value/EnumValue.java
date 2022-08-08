package net.kunmc.lab.configlib.value;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public final class EnumValue<T extends Enum<T>> extends AbstractEnumValue<T, EnumValue<T>> {

    public EnumValue(@NotNull T value) {
        super(value);
    }

    public EnumValue(@NotNull T value, T[] constants) {
        super(value, constants);
    }

    public EnumValue(@NotNull T value, Predicate<T> filter) {
        super(value, filter);
    }

    public EnumValue(@NotNull T value, T[] constants, Predicate<T> filter) {
        super(value, constants, filter);
    }
}
