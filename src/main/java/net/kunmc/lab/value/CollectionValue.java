package net.kunmc.lab.value;

import java.util.Collection;

public interface CollectionValue<T extends Collection<E>, E> extends Value<T> {
    boolean addableByCommand();

    boolean removableByCommand();

    boolean clearableByCommand();

    boolean validateOnAdd(E element);

    boolean validateOnRemove(E element);

    String failAddMessage(String entryName, E element);

    String succeedAddMessage(String entryName, E element);

    String failRemoveMessage(String entryName, E element);

    String succeedRemoveMessage(String entryName, E element);

    String clearMessage(String entryName);
}

