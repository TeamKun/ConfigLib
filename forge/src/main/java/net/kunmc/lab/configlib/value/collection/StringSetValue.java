package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.stringArgument(name, type, sb -> {
            allowableStringList.stream()
                    .filter(s -> !value.contains(s))
                    .forEach(sb::suggest);
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSource sender) {
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
    protected Set<String> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return Collections.singleton(argument.get(0).toString());
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument(name, sb -> {
            value.forEach(sb::suggest);
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です";
    }

    @Override
    protected Set<String> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        return Collections.singleton(argument.get(0).toString());
    }

    @Override
    protected String elementToString(String s) {
        return s;
    }
}
