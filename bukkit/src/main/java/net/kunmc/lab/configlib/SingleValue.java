package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class SingleValue<E, T extends SingleValue<E, T>> extends Value<E, T> {
    private transient final List<BiFunction<E, CommandContext, Boolean>> listeners = new ArrayList<>();
    private transient boolean writable = true;

    public SingleValue(E value) {
        super(value);
    }

    protected boolean writableByCommand() {
        return writable;
    }

    public T writableByCommand(boolean writable) {
        this.writable = writable;
        return ((T) this);
    }

    protected abstract void appendArgument(ArgumentBuilder builder);

    protected abstract boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    protected abstract String incorrectArgumentMessage(List<Object> argument);

    protected abstract E argumentToValue(List<Object> argument, CommandSender sender);

    protected abstract boolean validateOnSet(E newValue);

    protected abstract String invalidValueMessage(String entryName, E newValue);

    public T onModify(Consumer<E> listener) {
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
        listeners.add(listener);
        return ((T) this);
    }

    protected boolean onModifyValue(E newValue, CommandContext ctx) {
        return listeners.stream()
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