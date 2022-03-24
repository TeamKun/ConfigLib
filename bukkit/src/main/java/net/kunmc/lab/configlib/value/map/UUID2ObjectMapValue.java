package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.MapValue;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.*;
import java.util.stream.Collectors;

public abstract class UUID2ObjectMapValue<V, T extends UUID2ObjectMapValue<V, T>> extends MapValue<UUID, V, T> {
    public UUID2ObjectMapValue() {
        this(new HashMap<>());
    }

    public UUID2ObjectMapValue(Map<UUID, V> value) {
        super(value);
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.entityArgument("player", false, true);
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender) {
        return !((List) argument.get(0)).isEmpty();
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return "指定されたプレイヤーは存在しないかオフラインです.";
    }

    @Override
    protected UUID argumentToKeyForPut(List<Object> argument, CommandSender sender) {
        return ((List<Entity>) argument.get(0)).get(0).getUniqueId();
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.unparsedArgument("target", sb -> {
            List<String> list = value.keySet().stream()
                    .map(Bukkit::getOfflinePlayer)
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@r");
            }

            list.forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender) {
        String sel = argument.get(0).toString();
        return !value.isEmpty() && sel.equals("@r") ||
                value.keySet().stream()
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.equals("@r")) {
            return "セレクターは@rのみ指定できます.";
        }

        return s + "は追加されていませんでした.";
    }

    @Override
    protected UUID argumentToKeyForRemove(List<Object> argument, CommandSender sender) {
        String s = argument.get(0).toString();

        if (s.equals("@r")) {
            List<UUID> list = new ArrayList<>(value.keySet());
            Collections.shuffle(list);
            return list.get(0);
        }

        return Bukkit.getOfflinePlayerIfCached(s).getUniqueId();
    }

    @Override
    protected String keyToString(UUID uuid) {
        return Optional.of(Bukkit.getOfflinePlayer(uuid))
                .map(OfflinePlayer::getName)
                .orElse("null");
    }
}
