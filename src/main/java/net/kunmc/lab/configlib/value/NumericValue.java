package net.kunmc.lab.configlib.value;

public interface NumericValue<T extends Comparable<T>> extends SingleValue<T> {
    T min();

    T max();

    @Override
    default boolean validateOnSet(T newValue) {
        return newValue.compareTo(min()) != -1 && newValue.compareTo(max()) != 1;
    }

    @Override
    default String invalidValueMessage(String entryName, T argument) {
        return entryName + "は" + min() + "以上" + max() + "以下の値を入力してください.";
    }
}
