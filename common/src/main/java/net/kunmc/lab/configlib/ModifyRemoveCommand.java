package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.CollectionValueRemoveCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;

import java.util.Collection;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, CollectionValue value) {
        super("remove");

        addPrerequisite(value::checkExecutable);
        for (Object definition : value.argumentDefinitionsForRemove()) {
            argument(builder -> {
                ((ArgumentApplier) definition).applyArgument(builder);

                builder.execute(ctx -> {
                    Collection removeValue;
                    try {
                        removeValue = ((ArgumentMapper<Collection>) definition).mapArgument(ctx);
                    } catch (ArgumentValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        Collection remaining = value.toRemoved(removeValue.toArray());
                        ConfigSchemaValidation.validate(schemaEntry, remaining);
                    } catch (InvalidValueException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    config.mutate(() -> {
                        value.dispatchRemove(removeValue);
                        ((Collection) value.value()).removeAll(removeValue);
                    });

                    ctx.sendSuccess(value.succeedMessageForRemove(new CollectionValueRemoveCommandMessageParameter<>(
                            schemaEntry.entryName(),
                            ctx,
                            removeValue)));
                });
            });
        }
    }
}
