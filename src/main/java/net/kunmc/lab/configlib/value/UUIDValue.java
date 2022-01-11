package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class UUIDValue implements SingleValue<UUID> {
    private String playerName;
    private UUID uuid;
    private final transient Consumer<UUID> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public UUIDValue() {
        this(((UUID) null));
    }

    public UUIDValue(Player player) {
        this(player.getUniqueId());
    }

    public UUIDValue(UUID value) {
        this(value, x -> {
        });
    }

    public UUIDValue(Player player, Consumer<UUID> onSet) {
        this(player.getUniqueId(), onSet);
    }

    public UUIDValue(UUID value, Consumer<UUID> onSet) {
        this.uuid = value;
        this.playerName = playerName();
        this.consumer = onSet;
    }

    @Override
    public UUID value() {
        return uuid;
    }

    @Override
    public void value(UUID value) {
        this.uuid = value;
        this.playerName = playerName();
    }

    public void value(Player player) {
        this.uuid = player.getUniqueId();
    }

    @Override
    @Internal
    public void onSetValue(UUID newValue) {
        consumer.accept(newValue);
    }

    @Override
    @Internal
    public boolean validateOnSet(UUID newValue) {
        return true;
    }

    @Override
    @Internal
    public boolean listable() {
        return listable;
    }

    public UUIDValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    @Internal
    public boolean writableByCommand() {
        return writable;
    }

    public UUIDValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    public @Nullable OfflinePlayer toOfflinePlayer() {
        if (uuid != null) {
            return Bukkit.getOfflinePlayer(uuid);
        }

        return null;
    }

    public @Nullable Player toPlayer() {
        if (uuid != null) {
            return Bukkit.getPlayer(uuid);
        }

        return null;
    }

    public String playerName() {
        if (uuid != null) {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }

        return "";
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,playerName=%s,listable=%b,writable=%b}", uuid, playerName, listable, writable);
    }

    @Override
    @Internal
    public String succeedSetMessage(String entryName) {
        return entryName + "の値を" + playerName() + "に設定しました.";
    }

    @Override
    @Internal
    public void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + playerName());
    }

    @Override
    @Internal
    public void appendArgument(UsageBuilder builder) {
        builder.entityArgument("target", true, false, sb -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.getUniqueId().equals(uuid))
                    .map(Player::getName)
                    .forEach(sb::suggest);
            sb.suggest("@r");
        });
    }

    @Override
    @Internal
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        List<Player> list = ((List<Player>) argument.get(0));

        if (list.size() != 1) {
            return false;
        }

        return !list.get(0).getUniqueId().equals(uuid);
    }

    @Override
    @Internal
    public UUID argumentToValue(List<Object> argument, CommandSender sender) {
        return ((List<Player>) argument.get(0)).get(0).getUniqueId();
    }

    @Override
    @Internal
    public String incorrectArgumentMessage(List<Object> argument) {
        List<Player> list = ((List<Player>) argument.get(0));

        if (list.isEmpty()) {
            return "プレイヤーが見つかりませんでした.";
        }

        if (list.size() > 1) {
            return "複数人のプレイヤーを設定することはできません.";
        }

        Player p = list.get(0);
        if (p.getUniqueId().equals(uuid)) {
            return p.getName() + "はすでに設定されているプレイヤーです.";
        }

        return "could not reach";
    }
}
