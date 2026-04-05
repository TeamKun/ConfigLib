package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.MapValueClearCommandMessageParameter;

import java.lang.reflect.Field;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(Field field, MapValue value) {
        super("clear");

        addPrerequisite(value::checkExecutable);

        execute(ctx -> {
            value.dispatchClear();
            value.clear();

            ctx.sendSuccess(value.succeedMessageForClear(new MapValueClearCommandMessageParameter(field.getName(),
                                                                                                  ctx)));
        });
    }
}
