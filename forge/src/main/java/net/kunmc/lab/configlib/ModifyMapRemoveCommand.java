package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.minecraft.command.CommandSource;

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
                CommandSource sender = ctx.getSender();
                if (!value.isCorrectKeyArgumentForRemove(entryName, argument, sender)) {
                    ctx.sendFailure(value.incorrectKeyArgumentMessageForRemove(entryName, argument, sender));
                    return;
                }

                Object k = value.argumentToKeyForRemove(argument, sender);
                if (!value.validateKeyForRemove(entryName, k, sender)) {
                    ctx.sendFailure(value.invalidKeyMessageForRemove(entryName, k, sender));
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
