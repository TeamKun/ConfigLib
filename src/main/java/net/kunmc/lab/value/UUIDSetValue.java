package net.kunmc.lab.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UUIDSetValue extends SetValue<UUID> {
    private transient Boolean listable = true;

    public UUIDSetValue(UUID... uuids) {
        this(Sets.newHashSet(uuids));
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
}
