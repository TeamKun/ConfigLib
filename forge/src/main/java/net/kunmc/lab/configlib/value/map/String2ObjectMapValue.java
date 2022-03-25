package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class String2ObjectMapValue<V, T extends String2ObjectMapValue<V, T>> extends MapValue<String, V, T> {
    private final transient List<String> allowableKeyStringList = new ArrayList<>();

    public String2ObjectMapValue(Map<String, V> value) {
        super(value);
    }

    public T addAllowableKeyString(@NotNull String s) {
        allowableKeyStringList.add(s);
        return ((T) this);
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE_QUOTED, sb -> {
            if (allowableKeyStringList.isEmpty()) {
                keySet().forEach(sb::suggest);
            } else {
                allowableKeyStringList.forEach(sb::suggest);
            }
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        if (allowableKeyStringList.isEmpty()) {
            return true;
        }

        return allowableKeyStringList.stream()
                .anyMatch(s -> s.equals(argument.get(0).toString()));
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected String argumentToKeyForPut(List<Object> argument, CommandSource sender) {
        return argument.get(0).toString();
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE, sb -> {
            keySet().forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return keySet().contains(argument.get(0).toString());
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(0) + "は登録されていないキーです.";
    }

    @Override
    protected String argumentToKeyForRemove(List<Object> argument, CommandSource sender) {
        return argument.get(0).toString();
    }

    @Override
    protected String keyToString(String s) {
        return s;
    }
}
