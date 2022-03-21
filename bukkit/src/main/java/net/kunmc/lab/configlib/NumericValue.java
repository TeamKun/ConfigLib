package net.kunmc.lab.configlib;

public abstract class NumericValue<E extends Number & Comparable<E>, T extends NumericValue<E, T>> extends SingleValue<E, T> {
    protected final E min;
    protected final E max;

    public NumericValue(E value, E min, E max) {
        super(value);

        this.min = min;
        this.max = max;
    }

    protected abstract E copyAdd(Number amount);

    protected abstract E copySub(Number amount);

    protected abstract int compareTo(Number n);

    @Override
    protected boolean validateOnSet(E newValue) {
        return newValue.compareTo(min) != -1 && newValue.compareTo(max) != 1;
    }

    @Override
    protected String invalidValueMessage(String entryName, E argument) {
        return min + "以上" + max + "以下の値を入力してください.";
    }
}
