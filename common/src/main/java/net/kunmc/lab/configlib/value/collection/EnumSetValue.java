package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class EnumSetValue<T extends Enum<T>> extends SetValue<T, EnumSetValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public EnumSetValue(Class<T> clazz) {
        this(clazz, x -> true);
    }

    public EnumSetValue(Class<T> clazz, @NotNull Predicate<T> filter) {
        super(new HashSet<>());

        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    protected List<ArgumentDefinition<Set<T>>> argumentDefinitionsForAdd() {
        List<ArgumentDefinition<Set<T>>> definitions = new ArrayList<>();
        definitions.add(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> {
            opt.validator(x -> !value.contains(x) && filter.test(x));
        }), (name, ctx) -> {
            return SetUtil.newHashSet(clazz.cast(name));
        }));
        return definitions;
    }

    @Override
    protected List<ArgumentDefinition<Set<T>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(x -> {
            return value.contains(x);
        })), (name, ctx) -> {
            return SetUtil.newHashSet(name);
        }));
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
