package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;
import java.util.List;

class ModifyMapRemoveCommand extends Command {
    public ModifyMapRemoveCommand(Field field, MapValue value, CommonBaseConfig config) {
        super("remove");

        argument(builder -> {
            String entryName = field.getName();

            value.appendKeyArgumentForRemove(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                if (!value.isCorrectKeyArgumentForRemove(entryName, argument, ctx)) {
                    ctx.sendFailure(value.incorrectKeyArgumentMessageForRemove(entryName, argument, ctx));
                    return;
                }

                Object k = value.argumentToKeyForRemove(argument, ctx);
                if (!value.validateKeyForRemove(entryName, k, ctx)) {
                    ctx.sendFailure(value.invalidKeyMessageForRemove(entryName, k, ctx));
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
