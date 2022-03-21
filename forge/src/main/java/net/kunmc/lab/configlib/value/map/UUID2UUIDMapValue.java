package net.kunmc.lab.configlib.value.map;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UUID2UUIDMapValue extends UUID2ObjectMapValue<UUID, UUID2UUIDMapValue> {
    private Stream<OfflinePlayer> getPlayerStreamForPut() {
        return Arrays.stream(Bukkit.getOfflinePlayers())
                .filter(p -> !onlyOnline || p.isOnline());
    }

    @Override
    protected void appendValueArgumentForPut(UsageBuilder builder) {
        builder.stringArgument("target2", StringArgument.Type.PHRASE, sb -> {
            List<String> list = getPlayerStreamForPut()
                    .map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@r");
            }

            sb.suggestAll(list);
        });
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        String sel = argument.get(1).toString();
        return sel.equals("@r") ||
                getPlayerStreamForPut()
                        .map(OfflinePlayer::getName)
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        String s = argument.get(1).toString();

        if (s.startsWith("@")) {
            return "セレクターは@rのみ指定できます.";
        }

        return "プレイヤーが見つかりませんでした.";
    }

    @Override
    protected UUID argumentToValueForPut(List<Object> argument, CommandSender sender) {
        String s = argument.get(1).toString();

        if (s.equals("@r")) {
            List<UUID> list = getPlayerStreamForPut()
                    .map(OfflinePlayer::getUniqueId)
                    .collect(Collectors.toList());
            Collections.shuffle(list);
            return list.get(0);
        }

        return Bukkit.getOfflinePlayerIfCached(s).getUniqueId();
    }

    @Override
    protected String valueToString(UUID uuid) {
        return keyToString(uuid);
    }
}
