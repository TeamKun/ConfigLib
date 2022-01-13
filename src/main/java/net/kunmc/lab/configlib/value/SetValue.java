package net.kunmc.lab.configlib.value;

import net.kunmc.lab.configlib.command.CollectionValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class SetValue<E> extends CollectionValue<Set<E>, E> implements Iterable<E> {
    protected transient boolean listable = true;
    protected transient boolean addable = true;
    protected transient boolean removable = true;
    protected transient boolean clearable = true;

    public SetValue(@NotNull Set<E> value) {
        super(value);
    }

    @Override
    protected boolean addableByCommand() {
        return addable;
    }

    public <T extends SetValue<E>> T addableByCommand(boolean addable) {
        this.addable = addable;
        return (T) this;
    }

    @Override
    protected boolean validateForAdd(Set<E> element) {
        return !value.containsAll(element);
    }

    @Override
    protected boolean removableByCommand() {
        return removable;
    }

    public <T extends SetValue<E>> T removableByCommand(boolean removable) {
        this.removable = removable;
        return (T) this;
    }

    @Override
    protected boolean validateForRemove(Set<E> element) {
        return value.containsAll(element);
    }

    @Override
    protected boolean clearableByCommand() {
        return clearable;
    }

    public <T extends SetValue<E>> T clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return (T) this;
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public <T extends SetValue<E>> T listable(boolean listable) {
        this.listable = listable;
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
