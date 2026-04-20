package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.util.Collection;

class ModifyClearCommand extends Command {
    public ModifyClearCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, CollectionValue value) {
        super("clear");

        addPrerequisite(value::checkExecutable);
        execute(ctx -> {
            try {
                Collection cleared = (Collection) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx);
                return;
            }

            try {
                config.mutate(() -> {
                    value.dispatchClear();
                    ((Collection) value.value()).clear();
                });
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx);
                return;
            }

            ctx.sendSuccess(value.succeedMessageForClear(new CollectionValueClearCommandMessageParameter(schemaEntry.entryName(),
                                                                                                         ctx)));
        });
    }
}
