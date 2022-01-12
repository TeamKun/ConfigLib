package net.kunmc.lab.configlib.value;

import net.kunmc.lab.configlib.command.SingleValue;

public abstract class NumericValue<T extends Comparable<T>> extends SingleValue<T> {
    public NumericValue(T value) {
        super(value);
    }

    protected abstract T min();

    protected abstract T max();

    @Override
    protected boolean validateOnSet(T newValue) {
        return newValue.compareTo(min()) != -1 && newValue.compareTo(max()) != 1;
    }

    @Override
    protected String invalidValueMessage(String entryName, T argument) {
        return entryName + "は" + min() + "以上" + max() + "以下の値を入力してください.";
    }
}
