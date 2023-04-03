package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class UUIDSetValue extends SetValue<UUID, UUIDSetValue> {
    public UUIDSetValue(UUID... uuids) {
        this(Sets.newHashSet(uuids));
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
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.uuidsArgumentWith("targets", option -> {
            option.filter(x -> {
                return x.size() > 1 || !value.contains(x.get(0));
            });
        });
    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }


    @Override
    protected Set<UUID> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return Sets.newHashSet(((List<UUID>) argument.get(0)));
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
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.uuidArgumentWith("target", option -> {
            option.filter(x -> {
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
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected Set<UUID> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Sets.newHashSet(((UUID) argument.get(0)));
    }

    @Override
    protected boolean validateForRemove(String entryName, Set<UUID> value, CommandContext ctx) {
        return true;
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<UUID> element, CommandContext ctx) {
        return "";
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
