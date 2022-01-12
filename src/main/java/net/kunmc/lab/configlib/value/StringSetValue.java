package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class StringSetValue extends SetValue<String> {
    public StringSetValue(String... strings) {
        this(new HashSet<>(Arrays.asList(strings)));
    }

    public StringSetValue(@NotNull Set<String> value) {
        super(value);
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    protected String succeedMessageForAdd(String entryName, Set<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    protected String succeedMessageForRemove(String entryName, Set<String> element) {
        return entryName + "から" + element.toArray(new String[0])[0] + "を削除しました.";
    }

    @Override
    protected String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream().collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("String");
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("String");
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected Set<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.get(0).toString());
    }

    @Override
    protected Set<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.get(0).toString());
    }
}
