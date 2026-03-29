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

    public EnumListValue(T... values) {
        this(x -> true, values);
    }

    public EnumListValue(@NotNull Predicate<T> filter, T... values) {
        super(new ArrayList<>());

        this.clazz = ((Class<T>) values.getClass()
                                       .getComponentType());
        this.filter = filter;
    }

    @Override
    protected List<ArgumentDefinition<List<T>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.filter(filter)),
                                                    (name, ctx) -> {
                                                        return ListUtil.of(name);
                                                    }));
    }

    @Override
    protected List<ArgumentDefinition<List<T>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.filter(x -> {
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
