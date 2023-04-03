package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.util.UUIDUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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
        builder.uuidsArgumentWith("targets", option -> {
            option.filter(x -> {
                return x.size() > 1 || !value.contains(x.get(0));
            });
        });
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
                      value.stream()
                           .filter(x -> ServerLifecycleHooks.getCurrentServer()
                                                            .getPlayerProfileCache()
                                                            .getProfileByUUID(x) == null)
                           .forEach(x -> sb.suggest(x.toString()));
                  });
        });
    }

    @Override
    protected Set<UUID> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Sets.newHashSet(((UUID) argument.get(0)));
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
