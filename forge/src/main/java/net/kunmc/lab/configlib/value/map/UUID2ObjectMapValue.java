package net.kunmc.lab.configlib.value.map;

import com.mojang.authlib.GameProfile;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;

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
        builder.stringArgument("player", StringArgument.Type.WORD, sb -> {
            value.keySet().stream()
                    .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid))
                    .map(GameProfile::getName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandSource sender) {
        String s = argument.get(0).toString();
        return value.keySet().stream()
                .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid))
                .map(GameProfile::getName)
                .anyMatch(x -> x.equals(s));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandSource sender) {
        return "指定されたプレイヤーは追加されていません.";
    }

    @Override
    protected UUID argumentToKeyForRemove(List<Object> argument, CommandSource sender) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                .getGameProfileForUsername(argument.get(0).toString()).getId();
    }

    @Override
    protected String keyToString(UUID uuid) {
        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid))
                .map(GameProfile::getName)
                .orElse("null");
    }
}
