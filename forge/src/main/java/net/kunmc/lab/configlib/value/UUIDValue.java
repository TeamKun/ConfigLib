package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.UUIDArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.UUIDUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class UUIDValue extends SingleValue<UUID, UUIDValue> {
    public UUIDValue() {
        this(((UUID) null));
    }

    public UUIDValue(PlayerEntity player) {
        this(player.getUniqueID());
    }

    public UUIDValue(UUID value) {
        super(value);
    }

    public @Nullable PlayerEntity toPlayer() {
        if (value == null) {
            return null;
        }
        return ServerLifecycleHooks.getCurrentServer()
                                   .getPlayerList()
                                   .getPlayerByUUID(value);
    }

    public String playerName() {
        if (value == null) {
            return "";
        }
        return valueToString(value);
    }

    @Override
    protected List<ArgumentDefinition<UUID>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new UUIDArgument("target"), (target, ctx) -> target));
    }

    @Override
    protected String valueToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,writable=%b}", value, writableByCommand());
    }
}
