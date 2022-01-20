package net.kunmc.lab.configlib;

public abstract class NumericValue<T extends Number & Comparable<T>> extends SingleValue<T> {
    public NumericValue(T value) {
        super(value);
    }

    protected abstract T min();

    protected abstract T max();

    protected abstract void add(Number amount);

    protected abstract void sub(Number amount);

    protected abstract int compareTo(Number n);

    @Override
    protected boolean validateOnSet(T newValue) {
        return newValue.compareTo(min()) != -1 && newValue.compareTo(max()) != 1;
    }

    @Override
    protected String invalidValueMessage(String entryName, T argument) {
        return min() + "以上" + max() + "以下の値を入力してください.";
    }
}
