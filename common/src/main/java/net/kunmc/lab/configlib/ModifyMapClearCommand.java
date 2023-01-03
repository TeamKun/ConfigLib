package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(Field field, MapValue value, CommonBaseConfig config) {
        super("clear");

        execute(ctx -> {
            if (value.onClearMap(ctx)) {
                return;
            }

            value.clear();
            ctx.sendSuccess(value.clearMessage(field.getName()));

            config.saveConfigIfPresent();
        });
    }
}
