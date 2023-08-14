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

    public final Optional<E> getOptional() {
        return Optional.ofNullable(value);
    }

    public final void ifPresent(Consumer<E> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public final void ifAbsent(Runnable runnable) {
        if (value == null) {
            runnable.run();
        }
    }

    public final void ifPresentOrElse(Consumer<E> consumer, Runnable runnable) {
        if (value != null) {
            consumer.accept(value);
        } else {
            runnable.run();
        }
    }

    public final <U> Optional<U> map(Function<E, U> mapper) {
        return getOptional().map(mapper);
    }

    public final boolean isPresent() {
        return value != null;
    }

    public final boolean isEmpty() {
        return value == null;
    }

    public final E orElse(E other) {
        if (value != null) {
            return value;
        }
        return other;
    }

    public final E orElseGet(Supplier<? extends E> supplier) {
        if (value != null) {
            return value;
        }
        return supplier.get();
    }

    public final T writableByCommand(boolean writable) {
        this.writable = writable;
        return ((T) this);
    }

    protected final boolean writableByCommand() {
        return writable;
    }

    protected abstract void appendArgument(ArgumentBuilder builder);

    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected abstract E argumentToValue(List<Object> argument, CommandContext ctx);

    protected boolean validateOnSet(String entryName, E newValue, CommandContext ctx) {
        return true;
    }

    protected String invalidValueMessage(String entryName, E newValue, CommandContext ctx) {
        return "";
    }

    /**
     * Add a listener fired on modify command.
     */
    public final T onModifyCommand(Consumer<E> listener) {
        modifyCommandListeners.add(listener);
        return ((T) this);
    }

    final void onModifyValueCommand(E newValue) {
        modifyCommandListeners.forEach(x -> x.accept(newValue));
    }

    protected String succeedModifyMessage(String entryName) {
        return entryName + "の値を" + valueToString(value()) + "に変更しました.";
    }

    @Override
    protected String asString() {
        if (value() == null) {
            return "null";
        }
        return valueToString(value());
    }

    protected abstract String valueToString(E e);
}
