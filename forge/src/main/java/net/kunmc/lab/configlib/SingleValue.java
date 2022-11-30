package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.*;

public abstract class SingleValue<E, T extends SingleValue<E, T>> extends Value<E, T> {
    private transient final List<BiFunction<E, CommandContext, Boolean>> modifyCommandListeners = new ArrayList<>();
    private transient final List<Consumer<E>> modifyListeners = new ArrayList<>();
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

    public E orElseThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public <X extends Throwable> E orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    protected boolean writableByCommand() {
        return writable;
    }

    public T writableByCommand(boolean writable) {
        this.writable = writable;
        return ((T) this);
    }

    protected abstract void appendArgument(ArgumentBuilder builder);

    protected abstract boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender);

    protected abstract String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender);

    protected abstract E argumentToValue(List<Object> argument, CommandSource sender);

    protected abstract boolean validateOnSet(String entryName, E newValue, CommandSource sender);

    protected abstract String invalidValueMessage(String entryName, E newValue, CommandSource sender);

    /**
     * set value after firing listeners registered by {@link net.kunmc.lab.configlib.SingleValue#onModify(Consumer)}
     */
    public final void setValueWithEvent(E value) {
        modifyListeners.forEach(x -> x.accept(value));
        super.value(value);
    }

    /**
     * add an event listener fired on modify command or {@link net.kunmc.lab.configlib.SingleValue#setValueWithEvent}.
     */
    public T onModify(Consumer<E> listener) {
        modifyListeners.add(listener);
        return onModify((v, ctx) -> {
            listener.accept(v);
        });
    }

    public T onModify(BiConsumer<E, CommandContext> listener) {
        return onModify((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public T onModify(BiFunction<E, CommandContext, Boolean> listener) {
        modifyCommandListeners.add(listener);
        return ((T) this);
    }

    protected boolean onModifyValue(E newValue, CommandContext ctx) {
        return modifyCommandListeners.stream()
                                     .map(x -> x.apply(newValue, ctx))
                                     .reduce(false, (a, b) -> a || b);
    }

    protected String succeedModifyMessage(String entryName) {
        return entryName + "の値を" + valueToString(value()) + "に変更しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        if (value() == null) {
            ctx.sendSuccess(entryName + ": null");
        } else {
            ctx.sendSuccess(entryName + ": " + valueToString(value()));
        }
    }

    protected abstract String valueToString(E e);
}
