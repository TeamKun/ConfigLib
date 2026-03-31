package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyClearCommand extends Command {
    public ModifyClearCommand(Field field, CollectionValue value) {
        super("clear");

        execute(ctx -> {
            value.onClearValue();
            ((Collection) value.value()).clear();

            ctx.sendSuccess(value.succeedMessageForClear(new CollectionValueClearCommandMessageParameter(field.getName(),
                                                                                                         ctx)));
        });
    }
}
