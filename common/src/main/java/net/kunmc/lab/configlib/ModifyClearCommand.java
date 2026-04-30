package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.CollectionValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

import java.util.Collection;

class ModifyClearCommand extends Command {
    public ModifyClearCommand(CommonBaseConfig config,
                              ConfigSchemaEntry<?> schemaEntry,
                              CollectionValue value,
                              ConfigCommandDescriptions.Provider descriptions) {
        super("clear");
        description(ConfigCommandDescriptions.clear(descriptions, schemaEntry.entryName()));

        addPrerequisite(value::checkExecutable);
        execute(ctx -> {
            try {
                Collection cleared = (Collection) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx, descriptions);
                return;
            }

            try {
                config.mutate(() -> {
                    value.dispatchClear();
                    ((Collection) value.value()).clear();
                }, ChangeTrace.command(ctx, "clear " + schemaEntry.entryName(), schemaEntry.entryName()));
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx, descriptions);
                return;
            }

            ctx.sendSuccess(value.succeedMessageForClear(new CollectionValueClearCommandMessageParameter(schemaEntry.entryName(),
                                                                                                         ctx,
                                                                                                         descriptions)));
        });
    }
}
