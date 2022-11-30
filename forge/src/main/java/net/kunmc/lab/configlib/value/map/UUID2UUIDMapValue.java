package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;
import java.util.UUID;

public class UUID2UUIDMapValue extends UUID2ObjectMapValue<UUID, UUID2UUIDMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.uuidArgument("target2");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSource sender) {
        return "";
    }

    @Override
    protected UUID argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((UUID) argument.get(1));
    }

    @Override
    protected String valueToString(UUID uuid) {
        return keyToString(uuid);
    }
}
