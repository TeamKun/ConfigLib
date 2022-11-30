package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.configlib.CollectionValue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ListValue<E, U extends ListValue<E, U>> extends CollectionValue<List<E>, E, U> implements Iterable<E> {
    protected transient boolean listable = true;
    protected transient boolean addable = true;
    protected transient boolean removable = true;
    protected transient boolean clearable = true;

    public ListValue(List<E> value) {
        super(value);
    }

    @Override
    public boolean addableByCommand() {
        return addable;
    }

    public U addableByCommand(boolean addable) {
        this.addable = addable;
        return (U) this;
    }

    @Override
    protected boolean validateForAdd(String entryName, List<E> value, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, List<E> value, CommandSender sender) {
        return "This message can't be shown.";
    }

    @Override
    public boolean removableByCommand() {
        return removable;
    }

    public U removableByCommand(boolean removable) {
        this.removable = removable;
        return (U) this;
    }

    @Override
    public boolean validateForRemove(String entryName, List<E> element, CommandSender sender) {
        return value.containsAll(element);
    }

    @Override
    protected final String invalidValueMessageForRemove(String entryName, List<E> value, CommandSender sender) {
        return String.format("%sは%sに追加されていませんでした.",
                             value.stream()
                                  .map(this::elementToString)
                                  .collect(Collectors.toList()),
                             entryName);
    }

    @Override
    public boolean clearableByCommand() {
        return clearable;
    }

    public U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return (U) this;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public U listable(boolean listable) {
        this.listable = listable;
        return (U) this;
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
