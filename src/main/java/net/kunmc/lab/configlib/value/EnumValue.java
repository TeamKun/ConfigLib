package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class EnumValue<T extends Enum<T>> extends SingleValue<T> {
    private transient Boolean listable = true;
    private transient Boolean writable = true;
    private final transient Class<T> clazz;

    public EnumValue(@NotNull T value) {
        super(value);

        clazz = (Class<T>) value.getClass();
    }

    private T[] constants() {
        return clazz.getEnumConstants();
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public EnumValue<T> writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .filter(x -> x != value)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(m -> m.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected T argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(m -> m.name().equalsIgnoreCase(argument.get(0).toString()))
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

    @Override
    protected boolean listable() {
        return listable;
    }

    public EnumValue<T> listable(boolean listable) {
        this.listable = listable;
        return this;
    }
}
