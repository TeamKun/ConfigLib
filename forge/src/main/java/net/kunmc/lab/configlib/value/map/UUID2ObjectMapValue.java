package net.kunmc.lab.configlib.value.map;

import com.mojang.authlib.GameProfile;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.MapValue;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
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
        builder.entityArgument("player", false, true);
    }

    @Override
    protected boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSource sender) {
        return !((List) argument.get(0)).isEmpty();
    }

    @Override
    protected String incorrectKeyArgumentMessageForPut(List<Object> argument) {
        return "指定されたプレイヤーは存在しないかオフラインです.";
    }

    @Override
    protected UUID argumentToKeyForPut(List<Object> argument, CommandSource sender) {
        return ((List<Entity>) argument.get(0)).get(0).getUniqueID();
    }

    @Override
    protected void appendKeyArgumentForRemove(ArgumentBuilder builder) {
        //builder.entityArgument("player", false, true);
        builder.stringArgument("player", StringArgument.Type.WORD, sb -> {
            value.keySet().stream()
                    .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid))
                    .map(GameProfile::getName)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSource sender) {
        String s = argument.get(0).toString();
        return value.keySet().stream()
                .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid))
                .map(GameProfile::getName)
                .anyMatch(x -> x.equals(s));
    }

    @Override
    protected String incorrectKeyArgumentMessageForRemove(List<Object> argument) {
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
