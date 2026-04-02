package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EnumListValue<T extends Enum<T>> extends ListValue<T, EnumListValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public EnumListValue(Class<T> clazz) {
        this(clazz, x -> true);
    }

    public EnumListValue(Class<T> clazz, @NotNull Predicate<T> filter) {
        super(new ArrayList<>());

        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    protected List<ArgumentDefinition<List<T>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(filter)),
                                                    (name, ctx) -> {
                                                        return ListUtil.of(name);
                                                    }));
    }

    @Override
    protected List<ArgumentDefinition<List<T>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(x -> {
            return value.contains(x);
        })), (name, ctx) -> {
            return ListUtil.of(name);
        }));
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
