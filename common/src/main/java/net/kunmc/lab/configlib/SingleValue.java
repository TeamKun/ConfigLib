package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SingleValue<E, T extends SingleValue<E, T>> extends Value<E, T> {
    private transient final List<Consumer<E>> modifyCommandListeners = new ArrayList<>();
    private transient boolean writable = true;

    public SingleValue(E value) {
        super(value);
    }

    public Optional<E> getOptional() {
        return Optional.ofNullable(value);
    }

    public void ifPresent(Consumer<E> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public void ifAbsent(Runnable runnable) {
        if (value == null) {
            runnable.run();
        }
    }

    public void ifPresentOrElse(Consumer<E> consumer, Runnable runnable) {
        if (value != null) {
            consumer.accept(value);
        } else {
            runnable.run();
        }
    }

    public <U> Optional<U> map(Function<E, U> mapper) {
        return getOptional().map(mapper);
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public E orElse(E other) {
        if (value != null) {
            return value;
        }
        return other;
    }

    public E orElseGet(Supplier<? extends E> supplier) {
        if (value != null) {
            return value;
        }
        return supplier.get();
    }

    protected boolean writableByCommand() {
        return writable;
    }

    public T writableByCommand(boolean writable) {
        this.writable = writable;
        return ((T) this);
    }

    protected abstract void appendArgument(ArgumentBuilder builder);

    protected abstract boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx);

    protected abstract String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx);

    protected abstract E argumentToValue(List<Object> argument, CommandContext ctx);

    protected abstract boolean validateOnSet(String entryName, E newValue, CommandContext ctx);

    protected abstract String invalidValueMessage(String entryName, E newValue, CommandContext ctx);

    /**
     * Set value after firing listeners.
     */
    public final void setValueWithEvent(E value) {
        modifyCommandListeners.forEach(x -> x.accept(value));
        super.value(value);
    }

    /**
     * Add a listener fired on modify command.
     */
    public T onModifyCommand(Consumer<E> listener) {
        modifyCommandListeners.add(listener);
        return ((T) this);
    }

    protected void onModifyValueCommand(E newValue) {
        modifyCommandListeners.forEach(x -> x.accept(newValue));
    }

    protected String succeedModifyMessage(String entryName) {
        return entryName + "の値を" + valueToString(value()) + "に変更しました.";
    }

    @Override
    protected String asString(CommandContext ctx) {
        if (value() == null) {
            return "null";
        }
        return valueToString(value());
    }

    protected abstract String valueToString(E e);
}
