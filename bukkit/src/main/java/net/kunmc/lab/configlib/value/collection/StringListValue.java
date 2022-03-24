package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import org.bukkit.command.CommandSender;
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
        });
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
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean validateForAdd(List<String> newValue) {
        if (allowableStringList.isEmpty()) {
            return true;
        }

        return !value.containsAll(newValue);
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument(name, sb -> {
            value.forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected String elementToString(String s) {
        return s;
    }
}