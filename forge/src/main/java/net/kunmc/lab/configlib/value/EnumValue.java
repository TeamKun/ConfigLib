package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EnumValue<T extends Enum<T>> extends SingleValue<T, EnumValue<T>> {
    private final transient T[] constants;

    public EnumValue(@NotNull T value) {
        super(value);

        constants = value.getDeclaringClass().getEnumConstants();
    }

    public EnumValue(@NotNull T value, T[] constants) {
        super(value);

        this.constants = constants;
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
    protected T argumentToValue(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(String entryName, T newValue, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, T newValue, CommandSource sender) {
        return newValue.name() + "は不正な値です.";
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
