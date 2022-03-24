package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnumListValue<T extends Enum<T>> extends ListValue<T, EnumListValue<T>> {
    private final transient T[] constants;

    public EnumListValue(T... values) {
        super(new ArrayList<>());

        constants = ((T[]) values.getClass().getComponentType().getEnumConstants());
    }

    public EnumListValue(@NotNull List<T> value, @NotNull T[] constants, T... values) {
        super(value);
        this.value.addAll(Arrays.asList(values));

        this.constants = constants;
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected List<T> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toList());
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value.stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected List<T> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toList());
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}