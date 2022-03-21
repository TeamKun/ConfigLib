package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.argument.UnparsedArgument;
import net.kunmc.lab.configlib.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UUIDSetValue extends SetValue<UUID, UUIDSetValue> {
    private transient boolean onlyOnline = true;

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

    private Stream<OfflinePlayer> getPlayerStreamForAdd() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> !value.contains(p.getUniqueId()))
                .filter(p -> !onlyOnline || p.isOnline());
    }

    @Override
    protected void appendArgumentForAdd(UsageBuilder builder) {
        UnparsedArgument argument = new UnparsedArgument("targets", () -> {
            List<String> list = getPlayerStreamForAdd()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@a");
                list.add("@r");
            }

            return list;
        });

        CommandUtil.addArgument(builder, argument);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        String sel = argument.get(0).toString();
        return ((sel.equals("@a") || sel.equals("@r")) && getPlayerStreamForAdd().findAny().isPresent()) ||
                getPlayerStreamForAdd()
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.equals("@a") || s.equals("@r")) {
            return "プレイヤーが見つかりませんでした.";
        }

        if (s.startsWith("@")) {
            return "セレクターは@aか@rのみを指定できます.";
        }

        return "プレイヤーが見つかりませんでした.";
    }


    @Override
    protected Set<UUID> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        String s = argument.get(0).toString();

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
    protected boolean validateForAdd(Set<UUID> element) {
        return !value.containsAll(element);
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return p.getName() + "はすでに" + entryName + "に追加されています.";
        }

        return element.size() + "人のプレイヤーはすでに" + entryName + "に追加されています";
    }

    @Override
    protected String succeedMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
            return entryName + "に" + p.getName() + "を追加しました.";
        }

        return entryName + "に" + element.size() + "人のプレイヤーを追加しました";
    }

    @Override
    protected void appendArgumentForRemove(UsageBuilder builder) {
        UnparsedArgument argument = new UnparsedArgument("targets", () -> {
            List<String> list = value.stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@a");
                list.add("@r");
            }

            return list;
        });

        CommandUtil.addArgument(builder, argument);
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        String sel = argument.get(0).toString();
        return sel.equals("@a") ||
                sel.equals("@r") ||
                value.stream()
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected Set<UUID> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
        String s = argument.get(0).toString();

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

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.startsWith("@")) {
            return "セレクターは@aか@rのみを指定できます.";
        }

        return s + "は追加されていませんでした.";
    }

    @Override
    protected boolean validateForRemove(Set<UUID> element) {
        return element.stream()
                .anyMatch(value::contains);
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            return elementToString(element.toArray(new UUID[0])[0]) + "は" + entryName + "に追加されていませんでした.";
        }

        return element.size() + "人のプレイヤーは" + entryName + "に追加されていませんでした.";
    }

    @Override
    protected String succeedMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            return entryName + "から" + elementToString(element.toArray(new UUID[0])[0]) + "を削除しました.";
        }

        return entryName + "から" + element.size() + "人のプレイヤーを削除しました.";
    }

    @Override
    protected String elementToString(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
}