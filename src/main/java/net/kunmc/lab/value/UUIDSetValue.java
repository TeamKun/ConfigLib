package net.kunmc.lab.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDSetValue extends SetValue<UUID> {
    private transient Boolean listable = true;

    public UUIDSetValue(UUID... uuids) {
        this(Sets.newHashSet(uuids));
    }

    public UUIDSetValue(Collection<Player> players) {
        this(players.toArray(new Player[0]));
    }

    public UUIDSetValue(Player... players) {
        this(Arrays.stream(players)
                .map(Player::getUniqueId)
                .collect(Collectors.toSet()));
    }

    public UUIDSetValue(Set<UUID> value) {
        super(value);
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public UUIDSetValue listable(boolean listable) {
        this.listable = listable;
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
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("PlayerName", sb -> {
            Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    public boolean isCorrectArgument(Object argument) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .map(OfflinePlayer::getName)
                .anyMatch(s -> s.equals(argument));
    }

    @Override
    public Set<UUID> argumentToValue(Object argument) {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> p.getName().equals(argument))
                .map(OfflinePlayer::getUniqueId)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean addableByCommand() {
        return false;
    }

    @Override
    public boolean removableByCommand() {
        return false;
    }

    @Override
    public boolean clearableByCommand() {
        return false;
    }

    @Override
    public boolean validateOnAdd(Set<UUID> element) {
        return !value.containsAll(element);
    }

    @Override
    public boolean validateOnRemove(Set<UUID> element) {
        return value.containsAll(element);
    }

    @Override
    public String invalidValueMessageOnAdd(String entryName, Set<UUID> element) {
        UUID uuid = element.toArray(new UUID[0])[0];
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        return p.getName() + "はすでに" + entryName + "に追加されています.";
    }

    @Override
    public String succeedMessageOnAdd(String entryName, Set<UUID> element) {
        UUID uuid = element.toArray(new UUID[0])[0];
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        return entryName + "に" + p.getName() + "を追加しました.";
    }

    @Override
    public String invalidValueMessageOnRemove(String entryName, Set<UUID> element) {
        UUID uuid = element.toArray(new UUID[0])[0];
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        return p.getName() + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageOnRemove(String entryName, Set<UUID> element) {
        UUID uuid = element.toArray(new UUID[0])[0];
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        return entryName + "から" + p.getName() + "を削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }
}
