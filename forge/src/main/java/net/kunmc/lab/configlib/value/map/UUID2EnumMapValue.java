package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class UUID2EnumMapValue<T extends Enum<T>> extends UUID2ObjectMapValue<T, UUID2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public UUID2EnumMapValue(Class<T> clazz) {
        this(clazz, x -> true);
    }

    public UUID2EnumMapValue(Class<T> clazz, Predicate<T> filter) {
        super(new HashMap<>());

        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    protected List<PutArgumentDefinition<UUID, T>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new EnumArgument<>("name",
                                                                                                   clazz,
                                                                                                   opt -> opt.validator(
                                                                                                           filter)),
                                                                                (t, ctx) -> t)));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
