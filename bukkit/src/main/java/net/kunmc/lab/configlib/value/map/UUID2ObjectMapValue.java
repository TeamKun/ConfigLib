package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
        builder.uuidArgument("target");
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected UUID argumentToKeyForPut(List<Object> argument, CommandContext ctx) {
        return ((UUID) argument.get(0));
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.uuidArgumentWith("target", option -> {
            List<UUID> offlinePlayerUUIDs = Arrays.stream(Bukkit.getOfflinePlayers())
                                                  .map(OfflinePlayer::getUniqueId)
                                                  .collect(Collectors.toList());
            option.filter(x -> {
                      return value.containsKey(x);
                  })
                  .additionalSuggestionAction(sb -> {
                      value.keySet()
                           .stream()
                           .filter(x -> !offlinePlayerUUIDs.contains(x))
                           .map(Object::toString)
                           .forEach(sb::suggest);
                  });
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected UUID argumentToKeyForRemove(List<Object> argument, CommandContext ctx) {
        return (UUID) argument.get(0);
    }

    @Override
    protected String keyToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }
}
