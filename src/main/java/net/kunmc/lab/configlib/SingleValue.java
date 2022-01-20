package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class SingleValue<T> extends Value<T> {
    private final transient List<BiFunction<T, CommandContext, Boolean>> listeners = new ArrayList<>();

    public SingleValue(T value) {
        super(value);
    }

    protected abstract boolean writableByCommand();

    protected abstract void appendArgument(UsageBuilder builder);

    protected abstract boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    protected abstract String incorrectArgumentMessage(List<Object> argument);

    protected abstract T argumentToValue(List<Object> argument, CommandSender sender);

    protected abstract boolean validateOnSet(T newValue);

    protected abstract String invalidValueMessage(String entryName, T newValue);

    public <U extends SingleValue<T>> U onModify(Consumer<T> listener) {
        return onModify((v, ctx) -> {
            listener.accept(v);
        });
    }

    public <U extends SingleValue<T>> U onModify(BiConsumer<T, CommandContext> listener) {
        return onModify((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends SingleValue<T>> U onModify(BiFunction<T, CommandContext, Boolean> listener) {
        listeners.add(listener);
        return ((U) this);
    }

    protected boolean onModifyValue(T newValue, CommandContext ctx) {
        return listeners.stream()
                .map(x -> x.apply(newValue, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String succeedModifyMessage(String entryName) {
        return entryName + "の値を" + value() + "に変更しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}