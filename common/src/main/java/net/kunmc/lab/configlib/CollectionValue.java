package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class CollectionValue<T extends Collection<E>, E, U extends CollectionValue<T, E, U>> extends Value<T, U> {
    private final transient List<Consumer<T>> addListeners = new ArrayList<>();
    private final transient List<Consumer<T>> removeListeners = new ArrayList<>();
    private final transient List<Runnable> clearListeners = new ArrayList<>();
    private transient boolean addable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;

    public CollectionValue(T value) {
        super(value);
    }

    public final U addableByCommand(boolean addable) {
        this.addable = addable;
        return ((U) this);
    }

    protected final boolean addableByCommand() {
        return addable;
    }

    protected abstract void appendArgumentForAdd(ArgumentBuilder builder);

    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected abstract T argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx);

    protected boolean validateForAdd(String entryName, T value, CommandContext ctx) {
        return true;
    }

    protected String invalidValueMessageForAdd(String entryName, T value, CommandContext ctx) {
        return "";
    }

    /**
     * Add a listener fired on add command.
     */
    public final U onAdd(Consumer<T> listener) {
        addListeners.add(listener);
        return ((U) this);
    }

    protected final void onAddValue(T newValue) {
        addListeners.forEach(x -> x.accept(newValue));
    }

    protected String succeedMessageForAdd(String entryName, T value) {
        return String.format("%sに%sを追加しました.", entryName, elementToString(((E[]) value.toArray())[0]));
    }

    public final U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    protected final boolean removableByCommand() {
        return removable;
    }

    protected abstract void appendArgumentForRemove(ArgumentBuilder builder);

    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected abstract T argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx);

    protected boolean validateForRemove(String entryName, T value, CommandContext ctx) {
        return true;
    }

    protected String invalidValueMessageForRemove(String entryName, T value, CommandContext ctx) {
        return "";
    }

    /**
     * Add a listener fired on remove command.
     */
    public final U onRemove(Consumer<T> listener) {
        removeListeners.add(listener);
        return ((U) this);
    }

    final void onRemoveValue(T newValue) {
        removeListeners.forEach(x -> x.accept(newValue));
    }

    protected String succeedMessageForRemove(String entryName, T value) {
        return String.format("%sから%sを削除しました.", entryName, elementToString(((E[]) value.toArray())[0]));
    }

    public final U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    protected final boolean clearableByCommand() {
        return clearable;
    }

    /**
     * Add a listener fired on clear command.
     */
    public final U onClear(Runnable listener) {
        clearListeners.add(listener);
        return ((U) this);
    }

    final void onClearValue() {
        clearListeners.forEach(Runnable::run);
    }

    protected abstract String elementToString(E e);

    @Override
    protected String asString(CommandContext ctx) {
        return "[" + value.stream()
                          .map(this::elementToString)
                          .collect(Collectors.joining(", ")) + "]";
    }
}
