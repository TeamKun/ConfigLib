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
    public String invalidValueMessageForAdd(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    public String succeedMessageForAdd(String entryName, Set<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    public String invalidValueMessageForRemove(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageForRemove(String entryName, Set<String> element) {
        return entryName + "から" + element.toArray(new String[0])[0] + "を削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream().collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    @Override
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    public boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    public Set<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.toString());
    }

    @Override
    public Set<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.toString());
    }
}
