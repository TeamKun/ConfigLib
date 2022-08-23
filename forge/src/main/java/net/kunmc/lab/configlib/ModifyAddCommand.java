package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.minecraft.command.CommandSource;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyAddCommand extends Command {
    public ModifyAddCommand(Field field, CollectionValue value, BaseConfig config) {
        super("add");

        String entryName = field.getName();

        argument(builder -> {
            value.appendArgumentForAdd(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                CommandSource sender = ctx.getSender();
                if (!value.isCorrectArgumentForAdd(entryName, argument, sender)) {
                    ctx.sendFailure(value.incorrectArgumentMessageForAdd(entryName, argument, sender));
                    return;
                }

                Collection newValue = value.argumentToValueForAdd(entryName, argument, sender);
                if (!value.validateForAdd(entryName, newValue, sender)) {
                    ctx.sendFailure(value.invalidValueMessageForAdd(entryName, newValue, sender));
                    return;
                }

                if (value.onAddValue(newValue, ctx)) {
                    return;
                }

                ((Collection) value.value()).addAll(newValue);
                ctx.sendSuccess(value.succeedMessageForAdd(entryName, newValue));

                config.saveConfigIfPresent();
            });
        });
    }
}
