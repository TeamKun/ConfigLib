package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.CollectionValueRemoveCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(Field field, CollectionValue value) {
        super("remove");

        addPrerequisite(value::checkExecutable);

        String entryName = value.resolveEntryName(field.getName());
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
                        value.validate(remaining);
                    } catch (InvalidValueException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    value.dispatchRemove(removeValue);
                    ((Collection) value.value()).removeAll(removeValue);

                    ctx.sendSuccess(value.succeedMessageForRemove(new CollectionValueRemoveCommandMessageParameter<>(
                            entryName,
                            ctx,
                            removeValue)));
                });
            });
        }
    }
}
