package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

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

    public <U extends SingleValue<T>> U onSet(BiConsumer<T, CommandContext> listener) {
        return onSet((v, ctx) -> {
            listener.accept(v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends SingleValue<T>> U onSet(BiFunction<T, CommandContext, Boolean> listener) {
        listeners.add(listener);
        return ((U) this);
    }

    protected boolean onSetValue(T newValue, CommandContext ctx) {
        return listeners.stream().anyMatch(x -> x.apply(newValue, ctx));
    }

    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + value() + "に設定しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + value());
    }
}