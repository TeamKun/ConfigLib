package net.kunmc.lab.configlib.value;

import com.google.common.collect.Lists;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.List;

public class ObjectValue<T extends Nameable> extends SingleValue<T, ObjectValue<T>> {
    private transient final Collection<? extends T> candidates;

    public ObjectValue(T initial, T candidate, T... candidates) {
        this(initial, Lists.asList(candidate, candidates));
    }

    public ObjectValue(T initial, Collection<? extends T> candidates) {
        super(initial);

        checkPreconditions(candidates);
        this.candidates = candidates;
    }

    private static void checkPreconditions(Collection<? extends Nameable> candidates) {
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("candidates is empty.");
        }

        if (candidates.stream()
                .map(Nameable::tabCompleteName)
                .distinct()
                .count() != candidates.size()) {
            throw new IllegalArgumentException("candidates has duplicate name elements.");
        }
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.PHRASE_QUOTED, sb -> {
            candidates.stream()
                    .map(Nameable::tabCompleteName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return candidates.stream()
                .map(Nameable::tabCompleteName)
                .anyMatch(x -> x.equals(argument.get(0)));
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return String.format("%sは候補に存在しません", argument.get(0).toString());
    }

    @Override
    protected T argumentToValue(List<Object> argument, CommandSource sender) {
        return candidates.stream()
                .filter(x -> x.tabCompleteName().equals(argument.get(0)))
                .findFirst()
                .get();
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
