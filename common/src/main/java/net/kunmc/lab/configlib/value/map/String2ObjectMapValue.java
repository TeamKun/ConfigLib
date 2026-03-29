package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.ListUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class String2ObjectMapValue<V, T extends String2ObjectMapValue<V, T>> extends MapValue<String, V, T> {
    private final transient List<String> allowableKeyStringList = new ArrayList<>();

    public String2ObjectMapValue(Map<String, V> value) {
        super(value);
    }

    public T addAllowableKeyString(@NotNull String s) {
        allowableKeyStringList.add(s);
        return ((T) this);
    }

    protected ArgumentDefinition<String> keyArgumentDefinitionForPut() {
        return new ArgumentDefinition<>(new StringArgument("string", opt -> {
            opt.suggestionAction(sb -> {
                   if (allowableKeyStringList.isEmpty()) {
                       keySet().forEach(sb::suggest);
                   } else {
                       allowableKeyStringList.forEach(sb::suggest);
                   }
               })
               .filter(x -> {
                   if (allowableKeyStringList.stream()
                                             .noneMatch(s -> s.equals(x))) {
                       throw new InvalidArgumentException(x + "は不正な引数です.");
                   }
               });
        }, StringArgument.Type.PHRASE_QUOTED), (name, ctx) -> {
            return name;
        });
    }

    @Override
    protected List<ArgumentDefinition<String>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new StringArgument("string", opt -> {
            opt.suggestionAction(sb -> {
                keySet().forEach(sb::suggest);
            });
        }, StringArgument.Type.PHRASE), (name, ctx) -> {
            return name;
        }));
    }

    @Override
    protected String keyToString(String s) {
        return s;
    }
}
