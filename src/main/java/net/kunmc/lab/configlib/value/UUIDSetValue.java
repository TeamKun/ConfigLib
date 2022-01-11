package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.command.argument.UnparsedArgument;
import net.kunmc.lab.configlib.util.CommandUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UUIDSetValue extends SetValue<UUID> {
    private boolean onlyOnline = true;

    public UUIDSetValue(UUID... uuids) {
        this(Sets.newHashSet(uuids));
    }

    public UUIDSetValue(Collection<Player> players) {
        this(players.stream()
                .map(Player::getUniqueId)
                .collect(Collectors.toSet()));
    }

    public UUIDSetValue(Set<UUID> value) {
        super(value);
    }

    public UUIDSetValue suggestOfflines() {
        this.onlyOnline = false;
        return this;
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream()
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .collect(Collectors.joining(",")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    @Override
    public void appendArgumentForAdd(UsageBuilder builder) {
        List<Argument<?>> arguments = CommandUtil.getArguments(builder);

        arguments.add(new UnparsedArgument("target", () -> {
            List<String> list = getPlayerStreamForAdd()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@a");
                list.add("@r");
            }

            return list;
        }));
    }

    @Override
    public void appendArgumentForRemove(UsageBuilder builder) {
        List<Argument<?>> arguments = CommandUtil.getArguments(builder);

        arguments.add(new UnparsedArgument("target", () -> {
            List<String> list = value.stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@a");
                list.add("@r");
            }

            return list;
        }));
    }

    @Override
    public boolean isCorrectArgumentForAdd(Object argument, CommandSender sender) {
        return argument.equals("@a") ||
                argument.equals("@r") ||
                getPlayerStreamForAdd()
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(argument));
    }

    @Override
    public boolean isCorrectArgumentForRemove(Object argument, CommandSender sender) {
        return argument.equals("@a") ||
                argument.equals("@r") ||
                value.stream()
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(argument));
    }

    @Override
    public String incorrectArgumentMessageForAdd(Object argument) {
        String s = argument.toString();

        if (s.startsWith("@")) {
            return "セレクターは@aか@rのみを指定できます.";
        }

        return "プレイヤーが見つかりませんでした.";
    }

    @Override
    public String incorrectArgumentMessageForRemove(Object argument) {
        String s = argument.toString();

        if (s.startsWith("@")) {
            return "セレクターは@aか@rのみを指定できます.";
        }

        return s + "は追加されていません.";
    }

    @Override
    public Set<UUID> argumentToValueForAdd(Object argument, CommandSender sender) {
        String s = argument.toString();

        if (s.equals("@a")) {
            return getPlayerStreamForAdd()
                    .map(OfflinePlayer::getUniqueId)
                    .collect(Collectors.toSet());
        }

        if (s.startsWith("@r")) {
            List<UUID> list = getPlayerStreamForAdd()
                    .map(OfflinePlayer::getUniqueId)
                    .collect(Collectors.toList());
            Collections.shuffle(list);
            return Sets.newHashSet(list.get(0));
        }

        return Sets.newHashSet(Bukkit.getOfflinePlayerIfCached(s).getUniqueId());
    }

    @Override
    public Set<UUID> argumentToValueForRemove(Object argument, CommandSender sender) {
        String s = argument.toString();

        if (s.equals("@a")) {
            return Sets.newHashSet(value);
        }

        if (s.equals("@r")) {
            List<UUID> list = value.stream().collect(Collectors.toList());
            Collections.shuffle(list);
            return Sets.newHashSet(list.get(0));
        }

        return Sets.newHashSet(Bukkit.getOfflinePlayerIfCached(s).getUniqueId());
    }

    private Stream<OfflinePlayer> getPlayerStreamForAdd() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> !value.contains(p.getUniqueId()))
                .filter(p -> !onlyOnline || p.isOnline());
    }

    @Override
    public boolean validateForAdd(Set<UUID> element) {
        return !value.containsAll(element);
    }

    @Override
    public boolean validateForRemove(Set<UUID> element) {
        return element.stream()
                .anyMatch(value::contains);
    }

    @Override
    public String invalidValueMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return p.getName() + "はすでに" + entryName + "に追加されています.";
        }

        return element.size() + "人のプレイヤーはすでに" + entryName + "に追加されています";
    }

    @Override
    public String succeedMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return entryName + "に" + p.getName() + "を追加しました.";
        }

        return entryName + "に" + element.size() + "人のプレイヤーを追加しました";
    }

    @Override
    public String invalidValueMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return p.getName() + "は" + entryName + "に追加されていませんでした.";
        }

        return element.size() + "人のプレイヤーは" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return entryName + "から" + p.getName() + "を削除しました.";
        }

        return entryName + "から" + element.size() + "人のプレイヤーを削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "からすべてのプレイヤーを削除しました.";
    }
}