package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2StringMapValue<T extends Enum<T>> extends Enum2ObjectMapValue<T, String, Enum2StringMapValue<T>> {
    public Enum2StringMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2StringMapValue(Map<T, String> value, T... t) {
        super(value, t);
    }

    @Override
    protected List<PutArgumentDefinition<T, String>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new StringArgument("string",
                                                                                                   StringArgument.Type.PHRASE),
                                                                                (s, ctx) -> {
                                                                                    return s;
                                                                                })));
    }

    @Override
    protected String valueToString(String s) {
        return s;
    }
}
