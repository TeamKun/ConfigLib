package net.kunmc.lab.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringListValue extends ListValue<String> {
    public StringListValue(String... strings) {
        this(Arrays.asList(strings));
    }

    public StringListValue(List<String> value) {
        super(value);
    }

    @Override
    public String invalidValueMessageOnAdd(String entryName, List<String> element) {
        return "";
    }

    @Override
    public String succeedMessageOnAdd(String entryName, List<String> element) {
        return entryName + "に" + element.toArray(new String[0])[0] + "を追加しました.";
    }

    @Override
    public String invalidValueMessageOnRemove(String entryName, List<String> element) {
        return element.toArray(new String[0])[0] + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageOnRemove(String entryName, List<String> element) {
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
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("StringArgument");
    }

    @Override
    public boolean isCorrectArgument(Object argument) {
        return true;
    }

    @Override
    public List<String> argumentToValue(Object argument) {
        return Collections.singletonList(argument.toString());
    }

}
