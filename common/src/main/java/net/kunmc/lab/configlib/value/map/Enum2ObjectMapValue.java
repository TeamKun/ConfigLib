package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class Enum2ObjectMapValue<T extends Enum<T>, V, U extends Enum2ObjectMapValue<T, V, U>> extends MapValue<T, V, U> {
    private transient final Class<T> clazz;
    private transient Predicate<T> filter = x -> true;

    public Enum2ObjectMapValue(Map<T, V> value, T... t) {
        super(value);

        clazz = ((Class<T>) t.getClass()
                             .getComponentType());
    }

    public U setKeyFilter(Predicate<T> filter) {
        this.filter = filter;
        return ((U) this);
    }

    protected ArgumentDefinition<T> keyArgumentDefinitionForPut() {
        return new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.filter(filter)), (name, ctx) -> {
            return name;
        });
    }

    @Override
    protected List<ArgumentDefinition<T>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.filter(x -> {
            return value.containsKey(x);
        })), (name, ctx) -> {
            return name;
        }));
    }

    @Override
    protected String keyToString(T t) {
        return t.name();
    }
}
