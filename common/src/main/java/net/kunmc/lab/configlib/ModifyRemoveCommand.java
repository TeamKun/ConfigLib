package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.configlib.command.CollectionValueRemoveCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(Field field, CollectionValue value) {
        super("remove");

        String entryName = field.getName();
        for (Object definition : value.argumentDefinitionsForRemove()) {
            argument(builder -> {
                ((ArgumentApplier) definition).applyArgument(builder);

                builder.execute(ctx -> {
                    Collection removeValue;
                    try {
                        removeValue = ((ArgumentMapper<Collection>) definition).mapArgument(ctx);
                    } catch (InvalidArgumentException e) {
                        e.toIncorrectArgumentInputException()
                         .sendMessage(ctx);
                        return;
                    }

                    try {
                        Collection remaining = value.toRemoved(removeValue.toArray());
                        value.validate(remaining);
                    } catch (InvalidValueException e) {
                        e.getMessages()
                         .forEach(ctx::sendFailure);
                        return;
                    }

                    value.onRemoveValue(removeValue);
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
