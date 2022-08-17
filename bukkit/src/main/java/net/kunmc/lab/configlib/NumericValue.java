package net.kunmc.lab.configlib;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class NumericValue<E extends Number & Comparable<E>, T extends NumericValue<E, T>> extends SingleValue<E, T> implements Comparable<E> {
    protected final E min;
    protected final E max;

    public NumericValue(E value, E min, E max) {
        super(value);

        this.min = min;
        this.max = max;
    }

    public abstract E plus(Number other);

    public abstract E plus(NumericValue<?, ?> other);

    public abstract E minus(Number other);

    public abstract E minus(NumericValue<?, ?> other);

    public abstract E multiply(Number other);

    public abstract E multiply(NumericValue<?, ?> other);

    public abstract E divide(Number other);

    public abstract E divide(NumericValue<?, ?> other);

    public abstract E mod(Number other);

    public abstract E mod(NumericValue<?, ?> other);

    public boolean isLessThan(Number other) {
        return value.doubleValue() < other.doubleValue();
    }

    public boolean isLessThanOrEqualTo(Number other) {
        return value.doubleValue() <= other.doubleValue();
    }

    public boolean isGreaterThan(Number other) {
        return value.doubleValue() > other.doubleValue();
    }

    public boolean isGreaterThanOrEqualTo(Number other) {
        return value.doubleValue() >= other.doubleValue();
    }

    public int intValue() {
        return value.intValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public byte byteValue() {
        return value.byteValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public short shortValue() {
        return value.shortValue();
    }

    @Override
    public final int compareTo(@NotNull E other) {
        return compare(other);
    }

    protected abstract E copyAdd(Number amount);

    protected abstract E copySub(Number amount);

    protected abstract int compare(Number n);

    @Override
    protected boolean validateOnSet(String entryName, E newValue, CommandSender sender) {
        return newValue.compareTo(min) != -1 && newValue.compareTo(max) != 1;
    }

    @Override
    protected String invalidValueMessage(String entryName, E argument, CommandSender sender) {
        return min + "以上" + max + "以下の値を入力してください.";
    }
}
