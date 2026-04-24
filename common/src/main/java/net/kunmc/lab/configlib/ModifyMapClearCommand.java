package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.MapValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

import java.util.Map;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, MapValue value) {
        super("clear");

        addPrerequisite(value::checkExecutable);
        execute(ctx -> {
            try {
                Map cleared = (Map) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx);
                return;
            }

            try {
                config.mutate(() -> {
                    value.dispatchClear();
                    value.clear();
                }, ChangeTrace.command(ctx, "clear " + schemaEntry.entryName(), schemaEntry.entryName()));
            } catch (ConfigValidationException e) {
                e.sendMessage(ctx);
                return;
            }

            ctx.sendSuccess(value.succeedMessageForClear(new MapValueClearCommandMessageParameter(schemaEntry.entryName(),
                                                                                                  ctx)));
        });
    }
}
