package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.UUIDArgument;
import net.kunmc.lab.commandlib.argument.UUIDsArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import net.kunmc.lab.configlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class UUIDSetValue extends SetValue<UUID, UUIDSetValue> {
    public UUIDSetValue(UUID... uuids) {
        this(SetUtil.newHashSet(uuids));
    }

    public UUIDSetValue(Collection<Player> players) {
        this(players.stream()
                    .map(Player::getUniqueId)
                    .collect(Collectors.toSet()));
    }

    public UUIDSetValue(Set<UUID> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<Set<UUID>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new UUIDsArgument("targets", opt -> {
            opt.filter(x -> x.size() > 1 || !value.contains(x.get(0)));
        }), (targets, ctx) -> SetUtil.newHashSet(targets)));
    }

    @Override
    protected String succeedMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            return entryName + "に" + elementToString(uuid) + "を追加しました.";
        }
        return entryName + "に" + element.size() + "個のUUIDを追加しました.";
    }

    @Override
    protected List<ArgumentDefinition<Set<UUID>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new UUIDArgument("target", opt -> {
            opt.filter(x -> {
                   return value.contains(x);
               })
               .additionalSuggestionAction(sb -> {
                   List<UUID> offlinePlayerUUIDs = Arrays.stream(Bukkit.getOfflinePlayers())
                                                         .map(OfflinePlayer::getUniqueId)
                                                         .collect(Collectors.toList());
                   value.stream()
                        .filter(offlinePlayerUUIDs::contains)
                        .forEach(x -> sb.suggest(x.toString()));
               });
        }), (target, ctx) -> SetUtil.newHashSet(target)));
    }

    @Override
    protected String succeedMessageForRemove(String entryName, Set<UUID> element) {
        return entryName + "から" + elementToString(element.toArray(new UUID[0])[0]) + "を削除しました.";
    }

    @Override
    protected String elementToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }
}
