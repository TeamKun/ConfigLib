package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MaterialSetValue extends SetValue<Material> {
    public MaterialSetValue(Material... materials) {
        this(Sets.newHashSet(materials));
    }

    public MaterialSetValue(@NotNull Set<Material> value) {
        super(value);
    }

    @Override
    @Internal
    public String invalidValueMessageForAdd(String entryName, Set<Material> element) {
        Material m = element.toArray(new Material[0])[0];
        return m.name() + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    @Internal
    public String succeedMessageForAdd(String entryName, Set<Material> element) {
        Material m = element.toArray(new Material[0])[0];
        return entryName + "に" + m.name() + "を追加しました.";
    }

    @Override
    @Internal
    public String invalidValueMessageForRemove(String entryName, Set<Material> element) {
        Material m = element.toArray(new Material[0])[0];
        return m.name() + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    @Internal
    public String succeedMessageForRemove(String entryName, Set<Material> element) {
        Material m = element.toArray(new Material[0])[0];
        return entryName + "から" + m.name() + "を削除しました.";
    }

    @Override
    @Internal
    public String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    @Override
    @Internal
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.textArgument("MaterialName", sb -> {
            Arrays.stream(Material.values())
                    .filter(m -> !value.contains(m))
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    @Internal
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.textArgument("MaterialName", sb -> {
            value.stream()
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    @Internal
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    @Internal
    public boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    @Internal
    public Set<Material> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .collect(Collectors.toSet());
    }

    @Override
    @Internal
    public Set<Material> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .collect(Collectors.toSet());
    }

    @Override
    @Internal
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(value.stream()
                .map(Material::name)
                .collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }
}
