package net.kunmc.lab.value;

import java.util.Collection;

public interface CollectionValue<T extends Collection<E>, E> extends Value<T> {
    boolean addableByCommand();

    boolean removableByCommand();

    boolean clearableByCommand();

    boolean validateOnAdd(T element);

    boolean validateOnRemove(T element);

    String failAddMessage(String entryName, T element);

    String succeedAddMessage(String entryName, T element);

    String failRemoveMessage(String entryName, T element);

    String succeedRemoveMessage(String entryName, T element);

    String clearMessage(String entryName);
}

