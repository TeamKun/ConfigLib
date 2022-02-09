package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class StringSetValue extends SetValue<String, StringSetValue> {
    protected transient String name = "string";
    protected transient StringArgument.Type type = StringArgument.Type.PHRASE;
    private final transient List<String> allowableStringList = new ArrayList<>();

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
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument(name, type, sb -> {
            sb.suggestAll(allowableStringList.stream()
                    .filter(s -> !value.contains(s))
                    .collect(Collectors.toList()));
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        if (allowableStringList.isEmpty()) {
            return true;
        }

        return allowableStringList.stream().anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument + "は不正な引数です";
    }

    @Override
    protected Set<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.get(0).toString());
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument(name, sb -> {
            sb.suggestAll(new ArrayList<>(value));
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です";
    }

    @Override
    protected Set<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.get(0).toString());
    }

    @Override
    protected String elementToString(String s) {
        return s;
    }
}
