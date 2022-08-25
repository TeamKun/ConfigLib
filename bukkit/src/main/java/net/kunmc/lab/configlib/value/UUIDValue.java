package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class UUIDValue extends SingleValue<UUID, UUIDValue> {
    public UUIDValue() {
        this(((UUID) null));
    }

    public UUIDValue(Player player) {
        this(player.getUniqueId());
    }

    public UUIDValue(UUID value) {
        super(value);
    }

    public @Nullable OfflinePlayer toOfflinePlayer() {
        if (value == null) {
            return null;
        }
        return Bukkit.getOfflinePlayer(value);
    }

    public @Nullable Player toPlayer() {
        if (value == null) {
            return null;
        }
        return Bukkit.getPlayer(value);
    }

    public String playerName() {
        if (value == null) {
            return "";
        }
        return valueToString(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.playerArgument("target");
    }

    @Override
    protected UUID argumentToValue(List<Object> argument, CommandSender sender) {
        return ((Player) argument.get(0)).getUniqueId();
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    @Override
    protected boolean validateOnSet(String entryName, UUID newValue, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, UUID newValue, CommandSender sender) {
        return newValue + "は不正な値です.";
    }

    @Override
    protected String valueToString(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
