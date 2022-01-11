package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringListValue extends ListValue<String> {
    public StringListValue(String... strings) {
        this(Arrays.stream(strings).collect(Collectors.toList()));
    }

    public StringListValue(List<String> value) {
        super(value);
    }

    @Override
    @Internal
    public String invalidValueMessageForAdd(String entryName, List<String> element) {
        return "";
    }

    @Override
    @Internal
    public String succeedMessageForAdd(String entryName, List<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    @Internal
    public String invalidValueMessageForRemove(String entryName, List<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    @Internal
    public String succeedMessageForRemove(String entryName, List<String> element) {
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
    public List<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    @Internal
    public List<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
