package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.util.NameableSet;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ObjectSetValue<T extends Nameable> extends SetValue<T, ObjectSetValue<T>> {
    private transient final Collection<? extends T> candidates;
    private transient final Predicate<? super T> filter;

    public ObjectSetValue(Collection<? extends T> candidates) {
        this(candidates, x -> true);
    }

    public ObjectSetValue(Collection<? extends T> candidates, Predicate<? super T> filter) {
        super(new NameableSet<>());

        this.candidates = candidates;
        this.filter = filter;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.objectArgument("name", candidates, x -> {
            if (value.stream()
                    .map(Nameable::tabCompleteName)
                    .anyMatch(y -> y.equals(x.tabCompleteName()))) {
                return false;
            }

            return filter == null || filter.test(x);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    @Override
    protected Set<T> argumentToValueForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(((T) argument.get(0)));
    }

    @Override
    protected boolean validateForRemove(String entryName, Set<T> element, CommandSender sender) {
        return true;
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.objectArgument("name", candidates, x -> {
            return value.stream()
                    .map(Nameable::tabCompleteName)
                    .anyMatch(y -> y.equals(x.tabCompleteName()));
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    @Override
    protected Set<T> argumentToValueForRemove(String entryName, List<Object> argument, CommandSender sender) {
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
