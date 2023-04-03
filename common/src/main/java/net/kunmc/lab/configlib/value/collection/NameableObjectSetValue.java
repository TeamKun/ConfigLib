package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.util.NameableSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class NameableObjectSetValue<T extends Nameable> extends SetValue<T, NameableObjectSetValue<T>> {
    private transient final Collection<? extends T> candidates;
    private transient final Predicate<? super T> filter;

    public NameableObjectSetValue(Collection<? extends T> candidates) {
        this(candidates, x -> true);
    }

    public NameableObjectSetValue(Collection<? extends T> candidates, Predicate<? super T> filter) {
        super(new NameableSet<>());

        this.candidates = candidates;
        this.filter = filter;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.nameableObjectArgument("name", candidates, x -> {
            if (value.stream()
                     .map(Nameable::tabCompleteName)
                     .anyMatch(y -> y.equals(x.tabCompleteName()))) {
                return false;
            }

            return filter == null || filter.test(x);
        });
    }

    @Override
    protected Set<T> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return Sets.newHashSet(((T) argument.get(0)));
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.nameableObjectArgument("name", candidates, x -> {
            return value.stream()
                        .map(Nameable::tabCompleteName)
                        .anyMatch(y -> y.equals(x.tabCompleteName()));
        });
    }

    @Override
    protected Set<T> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Sets.newHashSet(((T) argument.get(0)));
    }

    @Override
    protected String elementToString(T t) {
        return t.tabCompleteName();
    }

    @Override
    public void value(Set<T> value) {
        this.value = new NameableSet<>(value);
    }
}
