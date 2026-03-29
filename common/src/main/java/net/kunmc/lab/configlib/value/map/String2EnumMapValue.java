package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

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
    protected List<PutArgumentDefinition<String, T>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new EnumArgument<>("name",
                                                                                                   clazz,
                                                                                                   opt -> {
                                                                                                       opt.filter(filter);
                                                                                                   }), (name, ctx) -> {
                                                           return name;
                                                       })));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
