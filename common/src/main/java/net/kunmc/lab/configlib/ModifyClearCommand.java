package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyClearCommand extends Command {
    public ModifyClearCommand(CommonBaseConfig config,
                              Field field,
                              ConfigSchemaEntry<?> schemaEntry,
                              CollectionValue value) {
        super("clear");

        addPrerequisite(value::checkExecutable);
        String entryName = value.resolveEntryName(field.getName());

        execute(ctx -> {
            try {
                Collection cleared = (Collection) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (InvalidValueException e) {
                e.sendMessage(ctx);
                return;
            }

            config.mutate(() -> {
                value.dispatchClear();
                ((Collection) value.value()).clear();
            });

            ctx.sendSuccess(value.succeedMessageForClear(new CollectionValueClearCommandMessageParameter(entryName,
                                                                                                         ctx)));
        });
    }
}
