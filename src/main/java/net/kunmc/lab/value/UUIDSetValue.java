package net.kunmc.lab.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
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
        super(value, "");
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
    public String failAddMessage(String entryName, UUID element) {
        return null;
    }

    @Override
    public String succeedAddMessage(String entryName, UUID element) {
        return null;
    }

    @Override
    public String failRemoveMessage(String entryName, UUID element) {
        return null;
    }

    @Override
    public String succeedRemoveMessage(String entryName, UUID element) {
        return null;
    }

    @Override
    public String clearMessage(String entryName) {
        return null;
    }
}
