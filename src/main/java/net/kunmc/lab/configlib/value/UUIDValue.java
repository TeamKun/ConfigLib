package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
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
    private String playerName;

    public UUIDValue() {
        this(((UUID) null));
    }

    public UUIDValue(Player player) {
        this(player.getUniqueId());
    }

    public UUIDValue(UUID value) {
        super(value);
        this.playerName = playerName();
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
        if (value != null) {
            return Bukkit.getOfflinePlayer(value).getName();
        }

        return "";
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.entityArgument("target", true, false, sb -> {
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
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
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
    protected String incorrectArgumentMessage(List<Object> argument) {
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
    protected boolean validateOnSet(UUID newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, UUID newValue) {
        return newValue + "は不正な値です.";
    }

    @Override
    protected String succeedModifyMessage(String entryName) {
        return entryName + "の値を" + playerName() + "に設定しました.";
    }

    @Override
    public void value(UUID value) {
        this.value = value;
        this.playerName = playerName();
    }

    public void value(Player player) {
        this.value = player.getUniqueId();
        this.playerName = player.getName();
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.success(entryName + ": " + playerName());
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,playerName=%s,listable=%b,writable=%b}", value, playerName, listable(), writableByCommand());
    }
}
