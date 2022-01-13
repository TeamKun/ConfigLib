package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CollectionValue<T extends Collection<E>, E> extends Value<T> {
    private final transient List<BiFunction<T, CommandContext, Boolean>> addListeners = new ArrayList<>();
    private final transient List<BiFunction<T, CommandContext, Boolean>> removeListeners = new ArrayList<>();
    private final transient List<Function<CommandContext, Boolean>> clearListeners = new ArrayList<>();

    public CollectionValue(T value) {
        super(value);
    }

    protected abstract boolean addableByCommand();

    protected abstract void appendArgumentForAdd(UsageBuilder builder);

    protected abstract boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender);

    protected abstract String incorrectArgumentMessageForAdd(List<Object> argument);

    protected abstract T argumentToValueForAdd(List<Object> argument, CommandSender sender);

    protected abstract boolean validateForAdd(T value);

    protected abstract String invalidValueMessageForAdd(String entryName, T value);

    public <U extends CollectionValue<T, E>> U onAdd(Consumer<T> listener) {
        return onAdd((v, ctx) -> {
            listener.accept(v);
        });
    }

    public <U extends CollectionValue<T, E>> U onAdd(BiConsumer<T, CommandContext> listener) {
        return onAdd((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends CollectionValue<T, E>> U onAdd(BiFunction<T, CommandContext, Boolean> listener) {
        addListeners.add(listener);
        return ((U) this);
    }

    protected boolean onAddValue(T newValue, CommandContext ctx) {
        return addListeners.stream()
                .map(x -> x.apply(newValue, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected abstract String succeedMessageForAdd(String entryName, T value);

    protected abstract boolean removableByCommand();

    protected abstract void appendArgumentForRemove(UsageBuilder builder);

    protected abstract boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender);

    protected abstract String incorrectArgumentMessageForRemove(List<Object> argument);

    protected abstract T argumentToValueForRemove(List<Object> argument, CommandSender sender);

    protected abstract boolean validateForRemove(T value);

    protected abstract String invalidValueMessageForRemove(String entryName, T value);

    public <U extends CollectionValue<T, E>> U onRemove(Consumer<T> listener) {
        return onRemove((v, ctx) -> {
            listener.accept(v);
        });
    }

    public <U extends CollectionValue<T, E>> U onRemove(BiConsumer<T, CommandContext> listener) {
        return onRemove((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends CollectionValue<T, E>> U onRemove(BiFunction<T, CommandContext, Boolean> listener) {
        removeListeners.add(listener);
        return ((U) this);
    }

    protected boolean onRemoveValue(T newValue, CommandContext ctx) {
        return removeListeners.stream()
                .map(x -> x.apply(newValue, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected abstract String succeedMessageForRemove(String entryName, T value);

    protected abstract boolean clearableByCommand();

    public <U extends CollectionValue<T, E>> U onClear(Runnable listener) {
        return onClear(ctx -> {
            listener.run();
        });
    }

    public <U extends CollectionValue<T, E>> U onClear(Consumer<CommandContext> listener) {
        return onClear(ctx -> {
            listener.accept(ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends CollectionValue<T, E>> U onClear(Function<CommandContext, Boolean> listener) {
        clearListeners.add(listener);
        return ((U) this);
    }

    protected boolean onClearValue(CommandContext ctx) {
        return clearListeners.stream()
                .map(x -> x.apply(ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected abstract String clearMessage(String entryName);
}
