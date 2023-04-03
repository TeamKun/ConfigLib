package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class String2EnumMapValue<T extends Enum<T>> extends String2ObjectMapValue<T, String2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public String2EnumMapValue(T... t) {
        this(x -> true, t);
    }

    public String2EnumMapValue(Predicate<T> filter, T... t) {
        super(new HashMap<>());

        this.clazz = ((Class<T>) t.getClass()
                                  .getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected T argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return clazz.cast(argument.get(0));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
