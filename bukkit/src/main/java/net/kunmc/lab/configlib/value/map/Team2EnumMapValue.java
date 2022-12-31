package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Team2EnumMapValue<T extends Enum<T>> extends Team2ObjectMapValue<T, Team2EnumMapValue<T>> {
    private final transient Class<T> clazz;
    private final transient Predicate<T> filter;

    public Team2EnumMapValue(T... e) {
        this(x -> true, e);
    }

    public Team2EnumMapValue(Predicate<T> filter, T... e) {
        super(new HashMap<>());

        this.clazz = ((Class<T>) e.getClass()
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
        return clazz.cast(argument.get(1));
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
