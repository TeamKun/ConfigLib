package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.UUID;

public class UUID2UUIDMapValue extends UUID2ObjectMapValue<UUID, UUID2UUIDMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.entityArgument("player2", false, true);
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSender sender) {
        return !((List) argument.get(1)).isEmpty();
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandSender sender) {
        return "指定されたプレイヤーは存在しないかオフラインです.";
    }

    @Override
    protected UUID argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((List<Entity>) argument.get(1)).get(0).getUniqueId();
    }

    @Override
    protected String valueToString(UUID uuid) {
        return keyToString(uuid);
    }
}
