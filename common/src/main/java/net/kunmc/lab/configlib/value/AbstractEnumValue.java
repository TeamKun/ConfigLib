package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public abstract class AbstractEnumValue<E extends Enum<E>, T extends AbstractEnumValue<E, T>> extends SingleValue<E, T> {
    private final transient Class<E> clazz;
    private transient BiFunction<E, CommandContext, Boolean> filter = (x, ctx) -> true;

    public AbstractEnumValue(@NotNull E value) {
        this(value, value.getDeclaringClass());
    }

    public AbstractEnumValue(@NotNull E value, Class<E> clazz) {
        super(value);
        this.clazz = clazz;
    }

    public T filter(BiFunction<E, CommandContext, Boolean> filter) {
        this.filter = filter;
        return ((T) this);
    }

    @Override
    protected List<ArgumentDefinition<E>> argumentDefinitions() {
        return List.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(filter)),
                                                (name, ctx) -> {
                                                    return name;
                                                }));
    }

    @Override
    protected String valueToString(E t) {
        return t.name();
    }
}
