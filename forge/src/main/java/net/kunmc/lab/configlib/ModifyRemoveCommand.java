package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyRemoveCommand extends Command {
    public ModifyRemoveCommand(Field field, CollectionValue value, BaseConfig config) {
        super("remove");

        String entryName = field.getName();

        usage(builder -> {
            value.appendArgumentForRemove(builder);

            builder.executes(ctx -> {
                List<Object> argument = ctx.getTypedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectArgumentForRemove(argument, sender)) {
                    ctx.fail(value.incorrectArgumentMessageForRemove(argument));
                    return;
                }

                Collection newValue = value.argumentToValueForRemove(argument, sender);
                if (!value.validateForRemove(newValue)) {
                    ctx.fail(value.invalidValueMessageForRemove(entryName, newValue));
                    return;
                }

                if (value.onRemoveValue(newValue, ctx)) {
                    return;
                }

                ((Collection) value.value()).removeAll(newValue);
                ctx.success(value.succeedMessageForRemove(entryName, newValue));

                config.saveConfigIfPresent();
            });
        });
    }
}
