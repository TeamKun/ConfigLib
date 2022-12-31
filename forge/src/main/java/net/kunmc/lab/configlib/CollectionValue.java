package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CollectionValue<T extends Collection<E>, E, U extends CollectionValue<T, E, U>> extends Value<T, U> {
    private final transient List<BiFunction<T, CommandContext, Boolean>> addListeners = new ArrayList<>();
    private final transient List<BiFunction<T, CommandContext, Boolean>> removeListeners = new ArrayList<>();
    private final transient List<Function<CommandContext, Boolean>> clearListeners = new ArrayList<>();
    private transient boolean addable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;

    public CollectionValue(T value) {
        super(value);
    }

    protected boolean addableByCommand() {
        return addable;
    }

    public U addableByCommand(boolean addable) {
        this.addable = addable;
        return ((U) this);
    }

    protected abstract void appendArgumentForAdd(ArgumentBuilder builder);

    protected abstract boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSource sender);

    protected abstract String incorrectArgumentMessageForAdd(String entryName,
                                                             List<Object> argument,
                                                             CommandSource sender);

    protected abstract T argumentToValueForAdd(String entryName, List<Object> argument, CommandSource sender);

    protected abstract boolean validateForAdd(String entryName, T value, CommandSource sender);

    protected abstract String invalidValueMessageForAdd(String entryName, T value, CommandSource sender);

    public U onAdd(Consumer<T> listener) {
        return onAdd((v, ctx) -> {
            listener.accept(v);
        });
    }

    public U onAdd(BiConsumer<T, CommandContext> listener) {
        return onAdd((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public U onAdd(BiFunction<T, CommandContext, Boolean> listener) {
        addListeners.add(listener);
        return ((U) this);
    }

    protected boolean onAddValue(T newValue, CommandContext ctx) {
        return addListeners.stream()
                           .map(x -> x.apply(newValue, ctx))
                           .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForAdd(String entryName, T value) {
        return String.format("%sに%sを追加しました.", entryName, elementToString(((E[]) value.toArray())[0]));
    }

    protected boolean removableByCommand() {
        return removable;
    }

    public U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    protected abstract void appendArgumentForRemove(ArgumentBuilder builder);

    protected abstract boolean isCorrectArgumentForRemove(String entryName,
                                                          List<Object> argument,
                                                          CommandSource sender);

    protected abstract String incorrectArgumentMessageForRemove(String entryName,
                                                                List<Object> argument,
                                                                CommandSource sender);

    protected abstract T argumentToValueForRemove(String entryName, List<Object> argument, CommandSource sender);

    protected abstract boolean validateForRemove(String entryName, T value, CommandSource sender);

    protected abstract String invalidValueMessageForRemove(String entryName, T value, CommandSource sender);

    public U onRemove(Consumer<T> listener) {
        return onRemove((v, ctx) -> {
            listener.accept(v);
        });
    }

    public U onRemove(BiConsumer<T, CommandContext> listener) {
        return onRemove((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public U onRemove(BiFunction<T, CommandContext, Boolean> listener) {
        removeListeners.add(listener);
        return ((U) this);
    }

    protected boolean onRemoveValue(T newValue, CommandContext ctx) {
        return removeListeners.stream()
                              .map(x -> x.apply(newValue, ctx))
                              .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForRemove(String entryName, T value) {
        return String.format("%sから%sを削除しました.", entryName, elementToString(((E[]) value.toArray())[0]));
    }

    protected boolean clearableByCommand() {
        return clearable;
    }

    public U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    public U onClear(Runnable listener) {
        return onClear(ctx -> {
            listener.run();
        });
    }

    public U onClear(Consumer<CommandContext> listener) {
        return onClear(ctx -> {
            listener.accept(ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public U onClear(Function<CommandContext, Boolean> listener) {
        clearListeners.add(listener);
        return ((U) this);
    }

    protected boolean onClearValue(CommandContext ctx) {
        return clearListeners.stream()
                             .map(x -> x.apply(ctx))
                             .reduce(false, (a, b) -> a || b);
    }

    protected final String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    protected abstract String elementToString(E e);

    @Override
    protected final void sendListMessage(CommandContext ctx, String entryName) {
        ctx.sendSuccess(entryName + ": [" + value.stream()
                                                 .map(this::elementToString)
                                                 .collect(Collectors.joining(", ")) + "]");
    }
}
