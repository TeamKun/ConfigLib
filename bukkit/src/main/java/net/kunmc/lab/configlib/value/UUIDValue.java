package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

    @Nullable
    public OfflinePlayer toOfflinePlayer() {
        if (value == null) {
            return null;
        }
        return Bukkit.getOfflinePlayer(value);
    }

    @Nullable
    public Player toPlayer() {
        if (value == null) {
            return null;
        }
        return Bukkit.getPlayer(value);
    }

    @Nullable
    public String playerName() {
        if (value == null) {
            return "";
        }
        return valueToString(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.uuidArgument("target");
    }

    @Override
    protected UUID argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((UUID) argument.get(0));
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected boolean validateOnSet(String entryName, UUID newValue, CommandContext ctx) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, UUID newValue, CommandContext ctx) {
        return newValue + "は不正な値です.";
    }

    @Override
    protected String valueToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
