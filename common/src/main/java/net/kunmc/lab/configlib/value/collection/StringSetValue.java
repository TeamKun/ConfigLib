package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StringSetValue extends SetValue<String, StringSetValue> {
    private final transient List<String> allowableStringList = new ArrayList<>();
    protected transient String name = "string";
    protected transient StringArgument.Type type = StringArgument.Type.PHRASE;

    public StringSetValue(String... strings) {
        this(new HashSet<>(Arrays.asList(strings)));
    }

    public StringSetValue(@NotNull Set<String> value) {
        super(value);
    }

    public StringSetValue addAllowableString(@NotNull String s) {
        allowableStringList.add(s);
        return this;
    }

    public StringSetValue name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public StringSetValue type(@NotNull StringArgument.Type type) {
        this.type = type;
        return this;
    }

    @Override
    protected List<ArgumentDefinition<Set<String>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new StringArgument(name, opt -> {
            opt.suggestionAction(sb -> {
                   allowableStringList.stream()
                                      .filter(s -> !value.contains(s))
                                      .forEach(sb::suggest);
               })
               .validator((x, ctx) -> {
                   if (allowableStringList.stream()
                                          .noneMatch(s -> s.equals(x))) {
                       throw new ArgumentValidationException(x + "は不正な引数です");
                   }
               });
        }, type), (s, ctx) -> {
            return SetUtil.newHashSet(s);
        }));
    }

    @Override
    protected List<ArgumentDefinition<Set<String>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new StringArgument(name, opt -> {
            opt.suggestionAction(sb -> {
                value.forEach(sb::suggest);
            });
        }), (s, ctx) -> {
            return SetUtil.newHashSet(s);
        }));
    }

    @Override
    protected String elementToString(String s) {
        return s;
    }
}
