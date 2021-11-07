package net.kunmc.lab.value;

public interface NumericValue<T extends Comparable<T>> extends Value<T> {
    T min();

    T max();

    @Override
    default boolean validate(T newValue) {
        return newValue.compareTo(min()) != -1 && newValue.compareTo(max()) != 1;
    }

    @Override
    default String failSetMessage(String entryName, T argument) {
        return entryName + "は" + min() + "以上" + max() + "以下の値を入力してください.";
    }
}
