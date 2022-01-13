package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
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
    protected void appendArgumentForAdd(UsageBuilder builder) {
        builder.stringArgument("String");
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, List<String> element) {
        return "";
    }

    @Override
    protected String succeedMessageForAdd(String entryName, List<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        builder.stringArgument("String");
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Override
    protected List<String> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return argument.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, List<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    protected String succeedMessageForRemove(String entryName, List<String> element) {
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
}
