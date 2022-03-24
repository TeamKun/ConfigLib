package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(Field field, MapValue value, BaseConfig config) {
        super("put");

        String entryName = field.getName();

        argument(builder -> {
            value.appendKeyArgumentForPut(builder);
            value.appendValueArgumentForPut(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectKeyArgumentForPut(argument, sender)) {
                    ctx.sendFailure(value.incorrectKeyArgumentMessageForPut(argument));
                    return;
                }
                if (!value.isCorrectValueArgumentForPut(argument, sender)) {
                    ctx.sendFailure((value.incorrectValueArgumentMessageForPut(argument)));
                    return;
                }

                Object k = value.argumentToKeyForPut(argument, sender);
                Object v = value.argumentToValueForPut(argument, sender);
                if (!value.validateKeyForPut(k)) {
                    ctx.sendFailure(value.invalidKeyMessageForPut(entryName, k));
                    return;
                }
                if (!value.validateValueForPut(v)) {
                    ctx.sendFailure(value.invalidValueMessageForPut(entryName, v));
                    return;
                }

                if (value.onPutValue(k, v, ctx)) {
                    return;
                }

                value.put(k, v);
                ctx.sendSuccess(value.succeedMessageForPut(entryName, k, v));

                config.saveConfigIfPresent();
            });
        });
    }
}
