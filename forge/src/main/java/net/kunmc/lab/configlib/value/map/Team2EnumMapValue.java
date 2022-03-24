package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.ScorePlayerTeam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Team2EnumMapValue<T extends Enum<T>> extends Team2ObjectMapValue<T, Team2EnumMapValue<T>> {
    private final transient Class<T> clazz;

    public Team2EnumMapValue(T... e) {
        this(new HashMap<>(), e);
    }

    public Team2EnumMapValue(Map<ScorePlayerTeam, T> map, T... e) {
        super(map);

        clazz = ((Class<T>) e.getClass().getComponentType());
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
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSource sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(1).toString()));
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
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
