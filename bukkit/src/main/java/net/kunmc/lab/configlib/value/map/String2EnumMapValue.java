package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class String2EnumMapValue<T extends Enum<T>> extends String2ObjectMapValue<T, String2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public String2EnumMapValue(T... t) {
        this(x -> true, t);
    }

    public String2EnumMapValue(Predicate<T> filter, T... t) {
        super(new HashMap<>());

        this.clazz = ((Class<T>) t.getClass()
                                  .getComponentType());
        this.filter = filter;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.enumArgument("name", clazz, filter);
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSender sender) {
        return argument.get(1) + "は不明な値です.";
    }

    @Override
    protected T argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return clazz.cast(argument.get(0));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
