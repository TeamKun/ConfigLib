package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(Field field, MapValue value, CommonBaseConfig config) {
        super("clear");

        execute(ctx -> {
            value.onClearMap();

            value.clear();
            ctx.sendSuccess(value.clearMessage(field.getName()));

            config.saveConfigIfPresent();
        });
    }
}
