package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.configlib.CollectionValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SetValue<E, U extends SetValue<E, U>> extends CollectionValue<Set<E>, E, U> implements Iterable<E> {
    public SetValue(@NotNull Set<E> value) {
        super(value);
    }

    @Override
    protected boolean validateForAdd(Set<E> element) {
        return !value.containsAll(element);
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, Set<E> element) {
        return String.format("%sはすでに%sに追加されています.", elementToString(((E[]) element.toArray())[0]), entryName);
    }

    @Override
    protected boolean validateForRemove(Set<E> element) {
        return value.containsAll(element);
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<E> element) {
        return String.format("%sは%sに追加されていませんでした.", elementToString(((E[]) element.toArray())[0]), entryName);
    }

    public int size() {
        return value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean contains(Object o) {
        return value.contains(o);
    }

    public Iterator<E> iterator() {
        return value.iterator();
    }

    @NotNull
    public Object[] toArray() {
        return value.toArray();
    }

    @NotNull
    public <T> T[] toArray(@NotNull T[] a) {
        return value.toArray(a);
    }

    public boolean add(E o) {
        return value.add(o);
    }

    public boolean remove(Object o) {
        return value.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return value.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return value.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return value.removeAll(c);
    }

    public void clear() {
        value.clear();
    }

    public boolean equals(Object o) {
        return value.equals(o);
    }

    public int hashCode() {
        return value.hashCode();
    }

    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(value, 0);
    }
}
