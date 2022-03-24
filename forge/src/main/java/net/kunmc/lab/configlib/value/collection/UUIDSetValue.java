package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.*;
import java.util.stream.Collectors;

public class UUIDSetValue extends SetValue<UUID, UUIDSetValue> {
    public UUIDSetValue(UUID... uuids) {
        this(Sets.newHashSet(uuids));
    }

    public UUIDSetValue(Collection<PlayerEntity> players) {
        this(players.stream()
                .map(Entity::getUniqueID)
                .collect(Collectors.toSet()));
    }

    public UUIDSetValue(Set<UUID> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.entityArgument("players", false, false);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(List<Object> argument, CommandSource sender) {
        return !((List) argument.get(0)).isEmpty();
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return "指定されたプレイヤーは存在しないかオフラインです.";
    }


    @Override
    protected Set<UUID> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return ((List<Entity>) argument.get(0)).stream()
                .map(Entity::getUniqueID).collect(Collectors.toSet());
    }

    @Override
    protected String invalidValueMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            return ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid).getName()
                    + "はすでに" + entryName + "に追加されています.";
        }

        return element.size() + "人のプレイヤーはすでに" + entryName + "に追加されています";
    }

    @Override
    protected String succeedMessageForAdd(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            UUID uuid = element.toArray(new UUID[0])[0];
            String name = ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                    .getProfileByUUID(uuid).getName();
            return entryName + "に" + name + "を追加しました.";
        }

        return entryName + "に" + element.size() + "人のプレイヤーを追加しました";
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.unparsedArgument("targets", sb -> {
            List<String> list = value.stream()
                    .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                            .getProfileByUUID(uuid).getName())
                    .collect(Collectors.toList());
            if (!list.isEmpty()) {
                list.add("@a");
                list.add("@r");
            }

            list.forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgumentForRemove(List<Object> argument, CommandSource sender) {
        String sel = argument.get(0).toString();
        return sel.equals("@a") ||
                sel.equals("@r") ||
                value.stream()
                        .map(uuid -> ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                                .getProfileByUUID(uuid).getName())
                        .anyMatch(s -> s.equals(sel));
    }

    @Override
    protected Set<UUID> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        String s = argument.get(0).toString();

        if (s.equals("@a")) {
            return Sets.newHashSet(value);
        }

        if (s.equals("@r")) {
            List<UUID> list = new ArrayList<>(value);
            Collections.shuffle(list);
            return Sets.newHashSet(list.get(0));
        }

        return Sets.newHashSet(ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                .getGameProfileForUsername(s).getId());
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        String s = argument.get(0).toString();

        if (s.startsWith("@")) {
            return "セレクターは@aか@rのみを指定できます.";
        }

        return s + "は追加されていませんでした.";
    }

    @Override
    protected boolean validateForRemove(Set<UUID> element) {
        return element.stream()
                .anyMatch(value::contains);
    }

    @Override
    protected String invalidValueMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            return elementToString(element.toArray(new UUID[0])[0]) + "は" + entryName + "に追加されていませんでした.";
        }

        return element.size() + "人のプレイヤーは" + entryName + "に追加されていませんでした.";
    }

    @Override
    protected String succeedMessageForRemove(String entryName, Set<UUID> element) {
        if (element.size() == 1) {
            return entryName + "から" + elementToString(element.toArray(new UUID[0])[0]) + "を削除しました.";
        }

        return entryName + "から" + element.size() + "人のプレイヤーを削除しました.";
    }

    @Override
    protected String elementToString(UUID uuid) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache()
                .getProfileByUUID(uuid).getName();
    }
}