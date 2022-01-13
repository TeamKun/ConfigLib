package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class CollectionValue<T extends Collection<E>, E> extends Value<T> {
    private final transient List<BiFunction<T, CommandContext, Boolean>> addListeners = new ArrayList<>();

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

    protected abstract String succeedMessageForRemove(String entryName, T value);

    protected abstract boolean clearableByCommand();

    protected abstract String clearMessage(String entryName);
}
