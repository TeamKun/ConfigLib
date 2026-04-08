package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class EnumSetValue<T extends Enum<T>> extends SetValue<T, EnumSetValue<T>> {
    private final transient Class<T> clazz;
    private transient BiFunction<T, CommandContext, Boolean> filterForAdd = (x, ctx) -> true;

    public EnumSetValue(Class<T> clazz) {
        super(new HashSet<>());
        this.clazz = clazz;
    }

    public EnumSetValue<T> filterForAdd(BiFunction<T, CommandContext, Boolean> filter) {
        this.filterForAdd = filter;
        return this;
    }

    @Override
    protected List<ArgumentDefinition<Set<T>>> argumentDefinitionsForAdd() {
        List<ArgumentDefinition<Set<T>>> definitions = new ArrayList<>();
        definitions.add(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> {
            opt.validator((x, ctx) -> !value.contains(x) && filterForAdd.apply(x, ctx));
        }), (name, ctx) -> {
            return Set.of(clazz.cast(name));
        }));
        return definitions;
    }

    @Override
    protected List<ArgumentDefinition<Set<T>>> argumentDefinitionsForRemove() {
        return List.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(x -> {
            return value.contains(x);
        })), (name, ctx) -> {
            return Set.of(name);
        }));
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
