package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.FloatArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2FloatMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, Float, Enum2FloatMapValue<T>> {
    public Enum2FloatMapValue(Class<T> kClass) {
        this(kClass, new HashMap<>());
    }

    public Enum2FloatMapValue(Class<T> kClass, Map<T, Float> value) {
        super(kClass, value);
    }

    @Override
    protected List<PutArgumentDefinition<T, Float>> argumentDefinitionsForPut() {
        return List.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                   new ArgumentDefinition<>(new FloatArgument("float"), (f, ctx) -> {
                                                       return f;
                                                   })));
    }

    @Override
    protected String valueToString(Float f) {
        return f.toString();
    }
}
