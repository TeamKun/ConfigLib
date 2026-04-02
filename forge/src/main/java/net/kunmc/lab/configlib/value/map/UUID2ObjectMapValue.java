package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.GameProfileArgument;
import net.kunmc.lab.commandlib.argument.UUIDArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.MapValue;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.UUIDUtil;
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

    protected ArgumentDefinition<UUID> keyArgumentDefinitionForPut() {
        return new ArgumentDefinition<>(new GameProfileArgument("player"), (p, ctx) -> p.getId());
    }

    @Override
    protected List<ArgumentDefinition<UUID>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new UUIDArgument("uuid", opt -> {
            opt.additionalSuggestionAction(sb -> {
                   value.keySet()
                        .stream()
                        .filter(x -> ServerLifecycleHooks.getCurrentServer()
                                                         .getPlayerProfileCache()
                                                         .getProfileByUUID(x) == null)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               })
               .validator(x -> {
                   return value.containsKey(x);
               });
        }), (uuid, ctx) -> uuid));
    }

    @Override
    protected String keyToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }
}
