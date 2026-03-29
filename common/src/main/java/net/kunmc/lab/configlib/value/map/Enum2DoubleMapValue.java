package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2DoubleMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Double, Enum2DoubleMapValue<T>> {
    public Enum2DoubleMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2DoubleMapValue(Map<T, Double> value, T... t) {
        super(value, t);
    }

    @Override
    protected List<PutArgumentDefinition<T, Double>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new DoubleArgument("double"),
                                                                                (d, ctx) -> {
                                                                                    return d;
                                                                                })));
    }

    @Override
    protected String valueToString(Double d) {
        return d.toString();
    }
}
