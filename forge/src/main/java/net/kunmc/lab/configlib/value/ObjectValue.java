package net.kunmc.lab.configlib.value;

import com.google.common.collect.Lists;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class ObjectValue<T extends Nameable> extends SingleValue<T, ObjectValue<T>> {
    private transient final Collection<? extends T> candidates;
    private transient final Predicate<? super T> filter;

    public ObjectValue(T initial, T candidate, T... candidates) {
        this(initial, Lists.asList(candidate, candidates));
    }

    public ObjectValue(T initial, Predicate<T> filter, T candidate, T... candidates) {
        this(initial, Lists.asList(candidate, candidates), filter);
    }

    public ObjectValue(T initial, Collection<? extends T> candidates) {
        this(initial, candidates, x -> true);
    }

    public ObjectValue(T initial, Collection<? extends T> candidates, Predicate<? super T> filter) {
        super(initial);

        this.candidates = candidates;
        this.filter = filter;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.objectArgument("name", candidates, filter);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected T argumentToValue(List<Object> argument, CommandSource sender) {
        return ((T) argument.get(0));
    }

    @Override
    protected boolean validateOnSet(String entryName, T newValue, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, T newValue, CommandSource sender) {
        return "";
    }

    @Override
    protected String valueToString(T t) {
        return value.tabCompleteName();
    }
}
