package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
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
    @Internal
    public String invalidValueMessageForAdd(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    @Internal
    public String succeedMessageForAdd(String entryName, Set<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    @Internal
    public String invalidValueMessageForRemove(String entryName, Set<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    @Internal
    public String succeedMessageForRemove(String entryName, Set<String> element) {
        return entryName + "から" + element.toArray(new String[0])[0] + "を削除しました.";
    }

    @Override
    @Internal
    public String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    @Internal
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream().collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    @Override
    @Internal
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    @Internal
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    @Internal
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    @Internal
    public boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    @Internal
    public Set<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.toString());
    }

    @Override
    @Internal
    public Set<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Collections.singleton(argument.toString());
    }
}
