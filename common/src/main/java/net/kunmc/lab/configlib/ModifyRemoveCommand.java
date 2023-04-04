package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(Field field, CollectionValue value) {
        super("remove");

        argument(builder -> {
            String entryName = field.getName();

            value.appendArgumentForRemove(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                if (!value.isCorrectArgumentForRemove(entryName, argument, ctx)) {
                    ctx.sendFailure(value.incorrectArgumentMessageForRemove(entryName, argument, ctx));
                    return;
                }

                Collection newValue = value.argumentToValueForRemove(entryName, argument, ctx);
                if (!value.validateForRemove(entryName, newValue, ctx)) {
                    ctx.sendFailure(value.invalidValueMessageForRemove(entryName, newValue, ctx));
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
