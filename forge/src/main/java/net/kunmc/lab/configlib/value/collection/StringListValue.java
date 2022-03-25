package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringListValue extends ListValue<String, StringListValue> {
    protected transient String name = "string";
    protected transient StringArgument.Type type = StringArgument.Type.PHRASE;
    private final transient List<String> allowableStringList = new ArrayList<>();

    public StringListValue(String... strings) {
        this(Arrays.stream(strings).collect(Collectors.toList()));
    }

    public StringListValue(List<String> value) {
        super(value);
    }

    public StringListValue name(@NotNull String name) {
        this.name = name;
        return this;
    }

    public StringListValue type(@NotNull StringArgument.Type type) {
        this.type = type;
        return this;
    }

    public StringListValue addAllowableString(@NotNull String s) {
        allowableStringList.add(s);
        return this;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.stringArgument(name, type, sb -> {
            allowableStringList.forEach(sb::suggest);
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSource sender) {
        if (allowableStringList.isEmpty()) {
            return true;
        }

        return allowableStringList.stream().anyMatch(s -> s.equals(argument.get(0)));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandSource sender) {
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean validateForAdd(String entryName, List<String> newValue, CommandSource sender) {
        if (allowableStringList.isEmpty()) {
            return true;
        }

        return !value.containsAll(newValue);
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument(name, sb -> {
            value.forEach(sb::suggest);
        }, null);
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected String elementToString(String s) {
        return s;
    }
}
