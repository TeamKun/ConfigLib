package net.kunmc.lab.configlib.value;


import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractEnumValue<E extends Enum<E>, T extends AbstractEnumValue<E, T>> extends SingleValue<E, T> {
    private final transient E[] constants;

    public AbstractEnumValue(@NotNull E value) {
        this(value, value.getDeclaringClass().getEnumConstants());
    }

    public AbstractEnumValue(@NotNull E value, E[] constants) {
        this(value, constants, x -> true);
    }

    public AbstractEnumValue(@NotNull E value, Predicate<E> filter) {
        this(value, value.getDeclaringClass().getEnumConstants(), filter);
    }

    public AbstractEnumValue(@NotNull E value, E[] constants, Predicate<E> filter) {
        super(value);

        this.constants = Arrays.stream(constants)
                .filter(filter)
                .toArray(x -> constants);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants)
                    .filter(x -> x != value)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected E argumentToValue(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(String entryName, E newValue, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, E newValue, CommandSource sender) {
        return newValue.name() + "は不正な値です.";
    }

    @Override
    protected String valueToString(E t) {
        return t.name();
    }
}
