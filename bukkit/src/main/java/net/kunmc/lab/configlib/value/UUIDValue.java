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
import java.util.stream.Collectors;

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
        if (value != null) {
            return Bukkit.getOfflinePlayer(value);
        }

        return null;
    }

    public @Nullable Player toPlayer() {
        if (value != null) {
            return Bukkit.getPlayer(value);
        }

        return null;
    }

    public String playerName() {
        if (value == null) {
            return "";
        }

        return valueToString(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.entityArgument("target", false, true, sb -> {
            List<String> list = Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.getUniqueId().equals(value))
                    .map(Player::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.forEach(sb::suggest);
                sb.suggest("@r");
            }
        });
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        List<Player> list = ((List<Player>) argument.get(0));

        if (list.size() != 1) {
            return false;
        }

        return !list.get(0).getUniqueId().equals(value);
    }

    @Override
    protected UUID argumentToValue(List<Object> argument, CommandSender sender) {
        return ((List<Player>) argument.get(0)).get(0).getUniqueId();
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        List<Player> list = ((List<Player>) argument.get(0));

        if (list.isEmpty()) {
            return "プレイヤーが見つかりませんでした.";
        }

        if (list.size() > 1) {
            return "複数人のプレイヤーを設定することはできません.";
        }

        Player p = list.get(0);
        if (p.getUniqueId().equals(value)) {
            return p.getName() + "はすでに設定されているプレイヤーです.";
        }

        return "could not reach";
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
