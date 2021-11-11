package net.kunmc.lab.configlib.value;

import java.util.Collection;

public interface CollectionValue<T extends Collection<E>, E> extends Value<T> {
    boolean addableByCommand();

    boolean removableByCommand();

    boolean clearableByCommand();

    boolean validateOnAdd(T element);

    boolean validateOnRemove(T element);

    String invalidValueMessageOnAdd(String entryName, T element);

    String succeedMessageOnAdd(String entryName, T element);

    String invalidValueMessageOnRemove(String entryName, T element);

    String succeedMessageOnRemove(String entryName, T element);

    String clearMessage(String entryName);
}

