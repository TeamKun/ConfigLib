package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapRemoveCommand extends Command {
    public ModifyMapRemoveCommand(Field field, MapValue value) {
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
                Object v = value.get(k);

                try {
                    if (v != null) {
                        Map map = new HashMap<>();
                        map.put(k, v);
                        value.validate(map);
                    }
                } catch (InvalidValueException e) {
                    e.getMessages()
                     .forEach(ctx::sendFailure);
                    return;
                }

                value.remove(k);
                value.onRemoveKey(k, v);

                ctx.sendSuccess(value.succeedMessageForRemove(entryName, k, v));
            });
        });
    }
}
