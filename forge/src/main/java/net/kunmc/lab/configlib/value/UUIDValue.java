package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
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
    protected void appendArgument(ArgumentBuilder builder) {
        builder.uuidArgument("target");
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected UUID argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((UUID) argument.get(0));
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected boolean validateOnSet(String entryName, UUID newValue, CommandContext ctx) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, UUID newValue, CommandContext ctx) {
        return "";
    }

    @Override
    protected String valueToString(UUID uuid) {
        return UUIDUtil.getNameOrUuid(uuid);
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
