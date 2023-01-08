package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(Field field, MapValue value) {
        super("clear");

        execute(ctx -> {
            value.onClearMap();
            value.clear();

            ctx.sendSuccess(field.getName() + "をクリアしました");
        });
    }
}
