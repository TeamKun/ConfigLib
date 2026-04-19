package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.command.MapValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.lang.reflect.Field;
import java.util.Map;

class ModifyMapClearCommand extends Command {
    public ModifyMapClearCommand(CommonBaseConfig config,
                                 Field field,
                                 ConfigSchemaEntry<?> schemaEntry,
                                 MapValue value) {
        super("clear");

        addPrerequisite(value::checkExecutable);
        String entryName = value.resolveEntryName(field.getName());

        execute(ctx -> {
            try {
                Map cleared = (Map) value.copyValue(value.value());
                cleared.clear();
                ConfigSchemaValidation.validate(schemaEntry, cleared);
            } catch (InvalidValueException e) {
                e.sendMessage(ctx);
                return;
            }

            config.mutate(() -> {
                value.dispatchClear();
                value.clear();
            });

            ctx.sendSuccess(value.succeedMessageForClear(new MapValueClearCommandMessageParameter(entryName, ctx)));
        });
    }
}
