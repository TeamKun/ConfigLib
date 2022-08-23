package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.minecraft.command.CommandSource;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(Field field, CollectionValue value, BaseConfig config) {
        super("remove");

        String entryName = field.getName();

        argument(builder -> {
            value.appendArgumentForRemove(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                CommandSource sender = ctx.getSender();
                if (!value.isCorrectArgumentForRemove(entryName, argument, sender)) {
                    ctx.sendFailure(value.incorrectArgumentMessageForRemove(entryName, argument, sender));
                    return;
                }

                Collection newValue = value.argumentToValueForRemove(entryName, argument, sender);
                if (!value.validateForRemove(entryName, newValue, sender)) {
                    ctx.sendFailure(value.invalidValueMessageForRemove(entryName, newValue, sender));
                    return;
                }

                if (value.onRemoveValue(newValue, ctx)) {
                    return;
                }

                ((Collection) value.value()).removeAll(newValue);
                ctx.sendSuccess(value.succeedMessageForRemove(entryName, newValue));

                config.saveConfigIfPresent();
            });
        });
    }
}
