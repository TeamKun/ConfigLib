package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public abstract class String2ObjectMapValue<V, T extends String2ObjectMapValue<V, T>> extends MapValue<String, V, T> {
    public String2ObjectMapValue(Map<String, V> value) {
        super(value);
    }

    @Override
    protected void appendKeyArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE_QUOTED, sb -> {
            keySet().forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected String argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return argument.get(0).toString();
    }

    @Override
    protected void appendKeyArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("string", StringArgument.Type.PHRASE, sb -> {
            keySet().forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        return keySet().contains(argument.get(0).toString());
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は登録されていないキーです.";
    }

    @Override
    protected String argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        return argument.get(0).toString();
    }

    @Override
    protected String keyToString(String s) {
        return s;
    }
}
