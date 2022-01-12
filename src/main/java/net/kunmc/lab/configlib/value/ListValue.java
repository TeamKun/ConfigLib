package net.kunmc.lab.configlib.value;

import net.kunmc.lab.configlib.command.CollectionValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public abstract class ListValue<E> extends CollectionValue<List<E>, E> implements Iterable<E> {
    protected transient boolean listable = true;
    protected transient boolean addable = true;
    protected transient boolean removable = true;
    protected transient boolean clearable = true;

    public ListValue(List<E> value) {
        super(value);
    }

    @Override
    public boolean validateForAdd(List<E> element) {
        return true;
    }

    @Override
    public boolean validateForRemove(List<E> element) {
        return value.contains(element);
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public <T extends ListValue<E>> T listable(boolean listable) {
        this.listable = listable;
        return (T) this;
    }

    @Override
    public boolean addableByCommand() {
        return addable;
    }

    public <T extends ListValue<E>> T addableByCommand(boolean addable) {
        this.addable = addable;
        return (T) this;
    }

    @Override
    public boolean removableByCommand() {
        return removable;
    }

    public <T extends ListValue<E>> T removableByCommand(boolean removable) {
        this.removable = removable;
        return (T) this;
    }

    @Override
    public boolean clearableByCommand() {
        return clearable;
    }

    public <T extends ListValue<E>> T clearableByCommand(boolean clearable) {
        this.clearable = clearable;
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

    @NotNull
    @Override
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

    public boolean add(E t) {
        return value.add(t);
    }

    public boolean remove(Object o) {
        return value.remove(o);
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        return value.containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends E> c) {
        return value.addAll(c);
    }

    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
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

    public E get(int index) {
        return value.get(index);
    }

    public E set(int index, E element) {
        return value.set(index, element);
    }

    public void add(int index, E element) {
        value.add(index, element);
    }

    public E remove(int index) {
        return value.remove(index);
    }

    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return value.lastIndexOf(o);
    }

    @NotNull
    public ListIterator<E> listIterator() {
        return value.listIterator();
    }

    @NotNull
    public ListIterator<E> listIterator(int index) {
        return value.listIterator(index);
    }

    @NotNull
    public List<E> subList(int fromIndex, int toIndex) {
        return value.subList(fromIndex, toIndex);
    }

    public Stream<E> stream() {
        return value.stream();
    }

    public Spliterator<E> spliterator() {
        return value.spliterator();
    }
}
