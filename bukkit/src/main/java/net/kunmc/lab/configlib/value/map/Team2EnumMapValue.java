package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Team2EnumMapValue<T extends Enum<T>> extends Team2ObjectMapValue<T, Team2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public Team2EnumMapValue(T... e) {
        this(x -> true, e);
    }

    public Team2EnumMapValue(Predicate<T> filter, T... e) {
        super(new HashMap<>());

        this.clazz = ((Class<T>) e.getClass()
                                  .getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected T argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return clazz.cast(argument.get(1));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
