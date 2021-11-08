package net.kunmc.lab.value;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public abstract class ListValue<T> implements Value<List<T>>, Iterable<T> {
    protected List<T> value;

    public ListValue(List<T> value) {
        this.value = value;
    }

    @Override
    public List<T> value() {
        return this.value;
    }

    @Override
    public void value(List<T> value) {
        this.value = value;
    }

    @Override
    public final void onSetValue(List<T> newValue) {

    }

    @Override
    public final boolean validate(List<T> newValue) {
        return true;
    }

    @Override
    public final boolean writableByCommand() {
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

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return value.iterator();
    }

    @NotNull
    public Object[] toArray() {
        return value.toArray();
    }

    @NotNull
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return value.toArray(a);
    }

    public boolean add(T t) {
        return value.add(t);
    }

    public boolean remove(Object o) {
        return value.remove(o);
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        return value.containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends T> c) {
        return value.addAll(c);
    }

    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return value.addAll(index, c);
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        return value.removeAll(c);
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        return value.retainAll(c);
    }

    public void clear() {
        value.clear();
    }

    public T get(int index) {
        return value.get(index);
    }

    public T set(int index, T element) {
        return value.set(index, element);
    }

    public void add(int index, T element) {
        value.add(index, element);
    }

    public T remove(int index) {
        return value.remove(index);
    }

    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return value.lastIndexOf(o);
    }

    @NotNull
    public ListIterator<T> listIterator() {
        return value.listIterator();
    }

    @NotNull
    public ListIterator<T> listIterator(int index) {
        return value.listIterator(index);
    }

    @NotNull
    public List<T> subList(int fromIndex, int toIndex) {
        return value.subList(fromIndex, toIndex);
    }

    public Stream<T> stream() {
        return value.stream();
    }

    public Spliterator<T> spliterator() {
        return value.spliterator();
    }
}
