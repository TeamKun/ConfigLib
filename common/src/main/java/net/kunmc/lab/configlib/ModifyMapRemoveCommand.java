package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.MapValueRemoveCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapRemoveCommand extends Command {
    public ModifyMapRemoveCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, MapValue value) {
        super("remove");

        addPrerequisite(value::checkExecutable);
        for (ArgumentDefinition<?> definition : ((List<ArgumentDefinition<?>>) value.argumentDefinitionsForRemove())) {
            argument(builder -> {
                definition.applyArgument(builder);

                builder.execute(ctx -> {
                    Object k;
                    try {
                        k = definition.mapArgument(ctx);
                    } catch (ArgumentValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    Object v = value.get(k);

                    try {
                        Map remaining = new HashMap<>(((Map) value.value()));
                        remaining.remove(k);
                        ConfigSchemaValidation.validate(schemaEntry, remaining);
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    config.mutate(() -> {
                        value.remove(k);
                        value.dispatchRemove(k, v);
                    });

                    ctx.sendSuccess(value.succeedMessageForRemove(new MapValueRemoveCommandMessageParameter<>(
                            schemaEntry.entryName(),
                            ctx,
                            k,
                            v)));
                });
            });
        }
    }
}
