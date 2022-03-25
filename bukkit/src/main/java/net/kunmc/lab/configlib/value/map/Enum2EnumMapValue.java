package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2EnumMapValue<K extends Enum<K>, V extends Enum<V>> extends Enum2ObjectMapValue<K, V, Enum2EnumMapValue<K, V>> {
    private final transient V[] constants;

    public Enum2EnumMapValue(Class<V> valueClass, K... k) {
        super(new HashMap<>(), k);

        constants = valueClass.getEnumConstants();
    }

    public Enum2EnumMapValue(Map<K, V> value, V[] constants, K... k) {
        super(value, k);

        this.constants = constants;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("value", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants)
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(1).toString()));
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(1) + "は不明な値です.";
    }

    @Override
    protected V argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(1).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected String valueToString(V v) {
        return v.name();
    }
}
