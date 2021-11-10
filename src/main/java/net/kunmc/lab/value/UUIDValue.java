package net.kunmc.lab.value;

import dev.kotx.flylib.command.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public class UUIDValue implements SingleValue<UUID> {
    private String playerName;
    private UUID uuid;
    private final transient Consumer<UUID> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

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
        this.playerName = Bukkit.getOfflinePlayer(value).getName();
        this.uuid = value;
        this.consumer = onSet;
    }

    @Override
    public UUID value() {
        return uuid;
    }

    @Override
    public void value(UUID value) {
        this.uuid = value;
        this.playerName = toOfflinePlayer().getName();
    }

    public void value(Player player) {
        this.uuid = player.getUniqueId();
    }

    @Override
    public void onSetValue(UUID newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validateOnSet(UUID newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    public UUIDValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    public boolean writableByCommand() {
        return writable;
    }

    public UUIDValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    public @NotNull OfflinePlayer toOfflinePlayer() {
        return Bukkit.getOfflinePlayer(value());
    }

    public @Nullable Player toPlayer() {
        return Bukkit.getPlayer(value());
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,playerName=%s,listable=%b,writable=%b}", uuid, playerName, listable, writable);
    }

    @Override
    public String succeedSetMessage(String entryName) {
        return entryName + "の値を" + toOfflinePlayer().getName() + "に設定しました.";
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + toOfflinePlayer().getName());
    }
}
