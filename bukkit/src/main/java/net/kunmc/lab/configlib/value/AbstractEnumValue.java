package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractEnumValue<E extends Enum<E>, T extends AbstractEnumValue<E, T>> extends SingleValue<E, T> {
    private final transient Class<E> clazz;
    private final transient Predicate<E> filter;

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

        this.clazz = ((Class<E>) constants.getClass().getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected E argumentToValue(List<Object> argument, CommandSender sender) {
        return clazz.cast(argument.get(0));
    }

    @Override
    protected boolean validateOnSet(String entryName, E newValue, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, E newValue, CommandSender sender) {
        return newValue.name() + "は不正な値です.";
    }

    @Override
    protected String valueToString(E t) {
        return t.name();
    }
}
