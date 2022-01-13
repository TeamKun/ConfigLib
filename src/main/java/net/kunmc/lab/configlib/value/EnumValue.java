package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class EnumValue<T extends Enum<T>> extends SingleValue<T> {
    public EnumValue(T value) {
        super(value);
    }

    private T[] constants() {
        return ((T[]) value.getClass().getEnumConstants());
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected T argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(T newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, T newValue) {
        return newValue.name() + "は不正な値です.";
    }
}
