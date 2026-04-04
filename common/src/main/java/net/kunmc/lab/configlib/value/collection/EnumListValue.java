package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class EnumListValue<T extends Enum<T>> extends ListValue<T, EnumListValue<T>> {
    private final transient Class<T> clazz;
    private transient BiFunction<T, CommandContext, Boolean> filterForAdd = (x, ctx) -> true;

    public EnumListValue(Class<T> clazz) {
        super(new ArrayList<>());
        this.clazz = clazz;
    }

    public EnumListValue<T> filterForAdd(BiFunction<T, CommandContext, Boolean> filter) {
        this.filterForAdd = filter;
        return this;
    }

    @Override
    protected List<ArgumentDefinition<List<T>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name",
                                                                       clazz,
                                                                       opt -> opt.validator(filterForAdd)),
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
