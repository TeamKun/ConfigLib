package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.EnumArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public abstract class AbstractEnumValue<E extends Enum<E>, T extends AbstractEnumValue<E, T>> extends SingleValue<E, T> {
    private final transient Class<E> clazz;
    private final transient Predicate<E> filter;

    public AbstractEnumValue(@NotNull E value) {
        this(value, value.getDeclaringClass());
    }

    public AbstractEnumValue(@NotNull E value, Class<E> clazz) {
        this(value, clazz, x -> true);
    }

    public AbstractEnumValue(@NotNull E value, Predicate<E> filter) {
        this(value, value.getDeclaringClass(), filter);
    }

    public AbstractEnumValue(@NotNull E value, Class<E> clazz, Predicate<E> filter) {
        super(value);

        this.clazz = clazz;
        this.filter = filter;
    }

    @Override
    protected List<ArgumentDefinition<E>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new EnumArgument<>("name", clazz, opt -> opt.validator(filter)),
                                                    (name, ctx) -> {
                                                        return name;
                                                    }));
    }

    @Override
    protected String valueToString(E t) {
        return t.name();
    }
}
