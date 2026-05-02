package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class SingleValue<E, T extends SingleValue<E, T>> extends Value<E, T> {
    private transient final List<Consumer<E>> modifyCommandListeners = new ArrayList<>();
    private transient boolean modifyEnabled = true;

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

    public final T disableModify() {
        this.modifyEnabled = false;
        return ((T) this);
    }

    protected final boolean isModifyEnabled() {
        return modifyEnabled;
    }

    protected abstract <A extends ArgumentApplier & ArgumentMapper<E>> List<A> argumentDefinitions();

    /**
     * Adds a listener fired only after this value is set by a generated command.
     * File reloads and programmatic changes do not trigger this command-specific listener.
     */
    public final T onSet(Consumer<E> listener) {
        modifyCommandListeners.add(listener);
        return ((T) this);
    }

    final void dispatchModifyCommand(E newValue) {
        modifyCommandListeners.forEach(x -> x.accept(newValue));
    }

    @Override
    protected String defaultDisplayString(@Nullable E e) {
        if (e == null) {
            return "null";
        }
        return valueToString(e);
    }

    protected abstract String valueToString(E e);
}
