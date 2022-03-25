package net.kunmc.lab.configlib.value;

import com.mojang.authlib.GameProfile;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
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
        if (value != null) {
            return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(value);
        }

        return null;
    }

    public String playerName() {
        if (value == null) {
            return "";
        }

        return valueToString(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.entityArgument("target", false, true);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return !((List) argument.get(0)).isEmpty();
    }

    @Override
    protected UUID argumentToValue(List<Object> argument, CommandSource sender) {
        return ((List<PlayerEntity>) argument.get(0)).get(0).getUniqueID();
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "指定されたプレイヤーは存在しないかオフラインです.";
    }

    @Override
    protected boolean validateOnSet(String entryName, UUID newValue, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, UUID newValue, CommandSource sender) {
        return newValue + "は不正な値です.";
    }

    @Override
    protected String valueToString(UUID uuid) {
        GameProfile profile = ServerLifecycleHooks.getCurrentServer().getPlayerProfileCache().getProfileByUUID(uuid);
        if (profile == null) {
            return "null";
        }

        return profile.getName();
    }

    @Override
    public String toString() {
        return String.format("UUIDValue{value=%s,listable=%b,writable=%b}", value, listable(), writableByCommand());
    }
}
