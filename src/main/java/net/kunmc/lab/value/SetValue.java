package net.kunmc.lab.value;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SetValue<T> implements Value<Set<T>>, Iterable<T> {
    protected Set<T> value;

    public SetValue(Set<T> value) {
        this.value = value;
    }

    @Override
    public Set<T> value() {
        return this.value;
    }

    @Override
    public void value(Set<T> value) {
        this.value = value;
    }

    @Override
    public final boolean writableByCommand() {
        return false;
    }

    @Override
    public final void onSetValue(Set<T> newValue) {

    }

    @Override
    public final boolean validate(Set<T> newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return false;
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

    public Iterator<T> iterator() {
        return value.iterator();
    }

    @NotNull
    public Object[] toArray() {
        return new Object[0];
    }

    @NotNull
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return null;
    }

    public boolean add(T o) {
        return value.add(o);
    }

    public boolean remove(Object o) {
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    public boolean addAll(Collection<? extends T> c) {
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

    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Spliterator<T> spliterator() {
        return Spliterators.spliterator(value, 0);
    }
}
