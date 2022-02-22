package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumSetValue<T extends Enum<T>> extends SetValue<T, EnumSetValue<T>> {
    private final transient T[] constants;

    public EnumSetValue(T... values) {
        super(new HashSet<>());

        constants = ((T[]) values.getClass().getComponentType().getEnumConstants());
    }

    public EnumSetValue(@NotNull Set<T> value, @NotNull T[] constants, T... values) {
        super(value);
        this.value.addAll(Sets.newHashSet(values));

        this.constants = ((T[]) values.getClass().getComponentType().getEnumConstants());
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants)
                    .filter(x -> value.stream().noneMatch(e -> x == e))
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toSet());
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value.stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toSet());
    }

    @Override
    protected String elementToString(T t) {
        return t.name();
    }
}
