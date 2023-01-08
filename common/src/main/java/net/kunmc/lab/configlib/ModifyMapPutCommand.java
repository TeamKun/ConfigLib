package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;

import java.lang.reflect.Field;
import java.util.List;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(Field field, MapValue value, CommonBaseConfig config) {
        super("put");

        argument(builder -> {
            String entryName = field.getName();

            value.appendKeyArgumentForPut(builder);
            value.appendValueArgumentForPut(builder);

            builder.execute(ctx -> {
                List<Object> argument = ctx.getParsedArgs();
                if (!value.isCorrectKeyArgumentForPut(entryName, argument, ctx)) {
                    ctx.sendFailure(value.incorrectKeyArgumentMessageForPut(entryName, argument, ctx));
                    return;
                }
                if (!value.isCorrectValueArgumentForPut(entryName, argument, ctx)) {
                    ctx.sendFailure((value.incorrectValueArgumentMessageForPut(entryName, argument, ctx)));
                    return;
                }

                Object k = value.argumentToKeyForPut(argument, ctx);
                Object v = value.argumentToValueForPut(argument, ctx);
                if (!value.validateKeyForPut(entryName, k, ctx)) {
                    ctx.sendFailure(value.invalidKeyMessageForPut(entryName, k, ctx));
                    return;
                }
                if (!value.validateValueForPut(entryName, v, ctx)) {
                    ctx.sendFailure(value.invalidValueMessageForPut(entryName, v, ctx));
                    return;
                }

                value.onPutValue(k, v);
                value.put(k, v);
               
                ctx.sendSuccess(value.succeedMessageForPut(entryName, k, v));
            });
        });
    }
}
