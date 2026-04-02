package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2IntegerMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Integer, Enum2IntegerMapValue<T>> {
    public Enum2IntegerMapValue(Class<T> kClass) {
        this(kClass, new HashMap<>());
    }

    public Enum2IntegerMapValue(Class<T> kClass, Map<T, Integer> value) {
        super(kClass, value);
    }

    @Override
    protected List<PutArgumentDefinition<T, Integer>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new IntegerArgument("integer"),
                                                                                (n, ctx) -> {
                                                                                    return n;
                                                                                })));
    }

    @Override
    protected String valueToString(Integer integer) {
        return integer.toString();
    }
}
