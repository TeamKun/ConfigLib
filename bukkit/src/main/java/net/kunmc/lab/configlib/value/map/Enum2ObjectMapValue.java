package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Enum2ObjectMapValue<T extends Enum<T>, V, U extends Enum2ObjectMapValue<T, V, U>> extends MapValue<T, V, U> {
    private transient final Class<T> clazz;

    public Enum2ObjectMapValue(Map<T, V> value, T... t) {
        super(value);

        clazz = ((Class<T>) t.getClass().getComponentType());
    }

    private T[] constants() {
        return clazz.getEnumConstants();
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected T argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value.keySet().stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected T argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected String keyToString(T t) {
        return t.name();
    }
}
