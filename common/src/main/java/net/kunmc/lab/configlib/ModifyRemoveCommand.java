package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
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
                    Collection newValue;
                    try {
                        newValue = ((ArgumentMapper<Collection>) definition).mapArgument(ctx);
                    } catch (InvalidArgumentException e) {
                        e.toIncorrectArgumentInputException()
                         .sendMessage(ctx);
                        return;
                    }

                    try {
                        value.validate(newValue);
                    } catch (InvalidValueException e) {
                        e.getMessages()
                         .forEach(ctx::sendFailure);
                        return;
                    }

                    value.onRemoveValue(newValue);
                    ((Collection) value.value()).removeAll(newValue);

                    ctx.sendSuccess(value.succeedMessageForRemove(entryName, newValue));
                });
            });
        }
    }
}
