package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.bukkit.command.CommandSender;

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
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectArgumentForRemove(argument, sender)) {
                    ctx.sendFailure(value.incorrectArgumentMessageForRemove(argument));
                    return;
                }

                Collection newValue = value.argumentToValueForRemove(argument, sender);
                if (!value.validateForRemove(newValue)) {
                    ctx.sendFailure(value.invalidValueMessageForRemove(entryName, newValue));
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
