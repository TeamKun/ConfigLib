package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2EnumMapValue<T extends Enum<T>> extends String2ObjectMapValue<T, String2EnumMapValue<T>> {
    private final transient T[] constants;

    public String2EnumMapValue(T... t) {
        this(new HashMap<>(), (T[]) t.getClass().getComponentType().getEnumConstants());
    }

    public String2EnumMapValue(Map<String, T> value, T[] constants) {
        super(value);

        this.constants = constants;
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
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
    protected T argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants)
                .filter(x -> x.name().equalsIgnoreCase(argument.get(1).toString()))
                .findFirst()
                .get();
    }

    @Override
    protected String valueToString(T t) {
        return t.name();
    }
}
