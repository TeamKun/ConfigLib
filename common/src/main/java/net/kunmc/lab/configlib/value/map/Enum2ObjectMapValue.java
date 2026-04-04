package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class Enum2ObjectMapValue<T extends Enum<T>, V, U extends Enum2ObjectMapValue<T, V, U>> extends MapValue<T, V, U> {
    private transient final Class<T> clazz;
    private transient BiFunction<T, CommandContext, Boolean> keyFilter = (x, ctx) -> true;

    public Enum2ObjectMapValue(Class<T> clazz, Map<T, V> value) {
        super(value);

        this.clazz = clazz;
    }

    public U filterForKey(BiFunction<T, CommandContext, Boolean> filter) {
        this.keyFilter = filter;
        return ((U) this);
    }

    protected ArgumentDefinition<T> keyArgumentDefinitionForPut() {
        return new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(keyFilter)),
                                        (name, ctx) -> {
                                            return name;
                                        });
    }

    @Override
    protected List<ArgumentDefinition<T>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(x -> {
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
