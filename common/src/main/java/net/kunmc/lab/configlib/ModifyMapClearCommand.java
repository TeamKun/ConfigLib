package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

import java.util.Map;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(CommonBaseConfig config,
                                 ConfigSchemaEntry<?> schemaEntry,
                                 MapValue value,
                                 ConfigCommandDescriptions.Provider descriptions) {
        super("clear");
        description(ConfigCommandDescriptions.clearMap(descriptions, schemaEntry.entryName()));

        addPrerequisite(value::checkExecutable);
        execute(ctx -> {
            try {
                Map cleared = (Map) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx, descriptions);
                return;
            }

            try {
                config.mutate(() -> {
                    value.dispatchClear();
                    value.clear();
                }, ChangeTrace.command(ctx, "clear " + schemaEntry.entryName(), schemaEntry.entryName()));
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx, descriptions);
                return;
            }

            ctx.sendSuccess(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.MAP_CLEAR_SUCCESS,
                                                  schemaEntry.entryName()));
        });
    }
}
