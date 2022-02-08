package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Enum2ObjectMapValue<T extends Enum<T>, V> extends MapValue<T, V> {
    private transient final Class<T> clazz;
    private transient boolean puttable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;
    private transient boolean listable = true;

    public Enum2ObjectMapValue(T... t) {
        this(new HashMap<>(), t);
    }

    public Enum2ObjectMapValue(Map<T, V> value, T... t) {
        super(value);

        clazz = ((Class<T>) t.getClass().getComponentType());
    }

    private T[] constants() {
        return clazz.getEnumConstants();
    }

    @Override
    protected boolean puttableByCommand() {
        return puttable;
    }

    public <U extends Enum2ObjectMapValue<T, V>> U puttableByCommand(boolean puttable) {
        this.puttable = puttable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
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
    protected boolean removableByCommand() {
        return removable;
    }

    public <U extends Enum2ObjectMapValue<T, V>> U removableByCommand(boolean removable) {
        this.removable = removable;
        return ((U) this);
    }

    @Override
    protected void appendKeyArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value.keySet().stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
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
    protected boolean clearableByCommand() {
        return clearable;
    }

    public <U extends Enum2ObjectMapValue<T, V>> U clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((U) this);
    }

    @Override
    protected String keyToString(T t) {
        return t.name();
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public <U extends Enum2ObjectMapValue<T, V>> U listable(boolean listable) {
        this.listable = listable;
        return ((U) this);
    }
}
