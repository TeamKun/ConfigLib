package net.kunmc.lab.configlib.value;

import com.google.common.collect.Lists;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.SingleValue;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class NameableObjectValue<T extends Nameable> extends SingleValue<T, NameableObjectValue<T>> {
    private transient final Collection<? extends T> candidates;
    private transient final Predicate<? super T> filter;

    public NameableObjectValue(T initial, T candidate, T... candidates) {
        this(initial, Lists.asList(candidate, candidates));
    }

    public NameableObjectValue(T initial, Predicate<T> filter, T candidate, T... candidates) {
        this(initial, Lists.asList(candidate, candidates), filter);
    }

    public NameableObjectValue(T initial, Collection<? extends T> candidates) {
        this(initial, candidates, x -> true);
    }

    public NameableObjectValue(T initial, Collection<? extends T> candidates, Predicate<? super T> filter) {
        super(initial);

        this.candidates = candidates;
        this.filter = filter;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.nameableObjectArgument("name", candidates, filter);
    }

    @Override
    protected T argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((T) argument.get(0));
    }

    @Override
    protected String valueToString(T t) {
        return value.tabCompleteName();
    }
}
