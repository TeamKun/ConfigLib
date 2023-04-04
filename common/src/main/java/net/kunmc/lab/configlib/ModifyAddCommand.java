package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyAddCommand extends Command {
    public ModifyAddCommand(Field field, CollectionValue value) {
        super("add");

        String entryName = field.getName();

        argument(builder -> {
            value.appendArgumentForAdd(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                if (!value.isCorrectArgumentForAdd(entryName, argument, ctx)) {
                    ctx.sendFailure(value.incorrectArgumentMessageForAdd(entryName, argument, ctx));
                    return;
                }

                Collection newValue = value.argumentToValueForAdd(entryName, argument, ctx);
                if (!value.validateForAdd(entryName, newValue, ctx)) {
                    ctx.sendFailure(value.invalidValueMessageForAdd(entryName, newValue, ctx));
                    return;
                }

                try {
                    value.validate(newValue);
                } catch (InvalidValueException e) {
                    e.getMessages()
                     .forEach(ctx::sendFailure);
                    return;
                }

                value.onAddValue(newValue);
                ((Collection) value.value()).addAll(newValue);

                ctx.sendSuccess(value.succeedMessageForAdd(entryName, newValue));
            });
        });
    }
}
