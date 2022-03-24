package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

class ModifyMapRemoveCommand extends Command {
    public ModifyMapRemoveCommand(Field field, MapValue value, BaseConfig config) {
        super("remove");

        String entryName = field.getName();

        argument(builder -> {
            value.appendKeyArgumentForRemove(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectKeyArgumentForRemove(argument, sender)) {
                    ctx.sendFailure(value.incorrectKeyArgumentMessageForRemove(argument));
                    return;
                }

                Object k = value.argumentToKeyForRemove(argument, sender);
                if (!value.validateKeyForRemove(k)) {
                    ctx.sendFailure(value.invalidKeyMessageForRemove(entryName, k));
                    return;
                }

                if (value.onRemoveKey(k, ctx)) {
                    return;
                }

                Object v = value.remove(k);
                ctx.sendSuccess(value.succeedMessageForRemove(entryName, k, v));

                config.saveConfigIfPresent();
            });
        });
    }
}