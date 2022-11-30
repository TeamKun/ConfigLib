package net.kunmc.lab.configlib.value.map;

import com.mojang.authlib.GameProfile;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.UUIDUtil;
import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class UUID2ObjectMapValue<V, T extends UUID2ObjectMapValue<V, T>> extends MapValue<UUID, V, T> {
    public UUID2ObjectMapValue() {
        this(new HashMap<>());
    }

    public UUID2ObjectMapValue(Map<UUID, V> value) {
        super(value);
    }

    @Override
    protected void appendKeyArgumentForPut(ArgumentBuilder builder) {
        builder.gameProfileArgument("player");
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected UUID argumentToKeyForPut(List<Object> argument, CommandSource sender) {
        return ((GameProfile) argument.get(0)).getId();
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        builder.uuidArgumentWith("target", option -> {
            option.additionalSuggestionAction(sb -> {
                      value.keySet()
                           .stream()
                           .filter(x -> ServerLifecycleHooks.getCurrentServer()
                                                            .getPlayerProfileCache()
                                                            .getProfileByUUID(x) == null)
                           .map(Object::toString)
                           .forEach(sb::suggest);
                  })
                  .filter(x -> value.containsKey(x));
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName,
                                                          List<Object> argument,
                                                          CommandSource sender) {
        return "";
    }

    @Override
    protected UUID argumentToKeyForRemove(List<Object> argument, CommandSource sender) {
        return ((UUID) argument.get(0));
    }

    @Override
    protected String keyToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }
}
