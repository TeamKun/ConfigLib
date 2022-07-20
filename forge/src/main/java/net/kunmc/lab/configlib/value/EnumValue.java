package net.kunmc.lab.configlib.value;

import org.jetbrains.annotations.NotNull;

public final class EnumValue<T extends Enum<T>> extends AbstractEnumValue<T, EnumValue<T>> {

    public EnumValue(@NotNull T value) {
        super(value);
    }

    public EnumValue(@NotNull T value, T[] constants) {
        super(value, constants);
    }
}
