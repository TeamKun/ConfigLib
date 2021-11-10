package net.kunmc.lab.value;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SetValue<E> implements CollectionValue<Set<E>, E>, Iterable<E> {
    protected Set<E> value;
    protected String name;

    public SetValue(@NotNull Set<E> value, @NotNull String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Set<E> value() {
        return this.value;
    }

    @Override
    public void value(Set<E> value) {
        this.value = value;
    }

    @Override
    public boolean validateOnAdd(E element) {
        return !value.contains(element);
    }

    @Override
    public boolean validateOnRemove(E element) {
        return value.contains(element);
    }

    @Override
    public String suffixName() {
        return name;
    }

    public <T extends SetValue<E>> T suffixName(String name) {
        this.name = name;
        return (T) this;
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
