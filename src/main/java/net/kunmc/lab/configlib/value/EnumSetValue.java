package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumSetValue<T extends Enum<T>> extends SetValue<T> {
    private final transient Class<?> enumClass;


    public EnumSetValue(T... values) {
        this(new HashSet<>(), values);
    }

    public EnumSetValue(@NotNull Set<T> value, T... values) {
        super(value);
        this.value.addAll(Sets.newHashSet(values));

        enumClass = values.getClass().getComponentType();
    }

    private T[] constants() {
        return ((T[]) enumClass.getEnumConstants());
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            Arrays.stream(constants())
                    .filter(x -> value.stream().noneMatch(e -> x == e))
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(m -> m.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(m -> m.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toSet());
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, Set<T> value) {
        Enum<?> e = value.toArray(new Enum<?>[0])[0];
        return e.name() + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    protected String succeedMessageForAdd(String entryName, Set<T> value) {
        Enum<?> e = value.toArray(new Enum<?>[0])[0];
        return entryName + "に" + e.name() + "を追加しました.";
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("name", StringArgument.Type.WORD, sb -> {
            value.stream()
                    .map(Enum::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .anyMatch(x -> x.name().equalsIgnoreCase(argument.get(0).toString()));
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument.get(0) + "は不明な値です.";
    }

    @Override
    protected Set<T> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(constants())
                .filter(x -> x.name().equalsIgnoreCase(argument.get(0).toString()))
                .collect(Collectors.toSet());
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<T> value) {
        Enum<?> e = value.toArray(new Enum<?>[0])[0];
        return e.name() + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    protected String succeedMessageForRemove(String entryName, Set<T> value) {
        Enum<?> e = value.toArray(new Enum<?>[0])[0];
        return entryName + "から" + e.name() + "を削除しました.";
    }

    @Override
    protected String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(value.stream()
                .map(Enum::name)
                .collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }
}
