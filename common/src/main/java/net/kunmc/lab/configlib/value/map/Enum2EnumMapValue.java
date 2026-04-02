package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Enum2EnumMapValue<K extends Enum<K>, V extends Enum<V>> extends Enum2ObjectMapValue<K, V, Enum2EnumMapValue<K, V>> {
    private final transient Class<V> clazz;
    private transient Predicate<V> filter = x -> true;

    public Enum2EnumMapValue(Class<K> keyClass, Class<V> valueClass) {
        super(keyClass, new HashMap<>());
        this.clazz = valueClass;
    }

    public Enum2EnumMapValue<K, V> setValueFilter(Predicate<V> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    protected List<PutArgumentDefinition<K, V>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new EnumArgument<>("value",
                                                                                                   clazz,
                                                                                                   opt -> {
                                                                                                       opt.validator(
                                                                                                               filter);
                                                                                                   }), (v, ctx) -> {
                                                           return v;
                                                       })));
    }

    @Override
    protected String valueToString(V v) {
        return v.name();
    }
}
