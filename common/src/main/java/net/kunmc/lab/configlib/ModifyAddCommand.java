package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.configlib.command.CollectionValueAddCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyAddCommand extends Command {
    public ModifyAddCommand(Field field, CollectionValue value) {
        super("add");

        String entryName = value.resolveEntryName(field.getName());
        for (Object definition : value.argumentDefinitionsForAdd()) {
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
                        Collection result = value.toAdded(newValue.toArray());
                        value.validate(result);
                    } catch (InvalidValueException e) {
                        e.getMessages()
                         .forEach(ctx::sendFailure);
                        return;
                    }

                    value.onAddValue(newValue);
                    ((Collection) value.value()).addAll(newValue);

                    ctx.sendSuccess(value.succeedMessageForAdd(new CollectionValueAddCommandMessageParameter<>(entryName,
                                                                                                               ctx,
                                                                                                               newValue)));
                });
            });
        }
    }
}
