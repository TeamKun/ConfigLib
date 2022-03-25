package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;

import java.util.*;

public class UUID2EnumMapValue<T extends Enum<T>> extends UUID2ObjectMapValue<T, UUID2EnumMapValue<T>> {
    private transient final Class<T> clazz;

    public UUID2EnumMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public UUID2EnumMapValue(Map<UUID, T> map, T... t) {
        super(map);

        clazz = ((Class<T>) t.getClass().getComponentType());
    }

    private T[] constants() {
        return clazz.getEnumConstants();
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(1).toString()));
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(1) + "は不明な値です.";
    }

    @Override
    protected T argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants())
                .filter(x -> x.name().equalsIgnoreCase(argument.get(1).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
