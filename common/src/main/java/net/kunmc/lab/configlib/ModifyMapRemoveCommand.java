package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapRemoveCommand extends Command {
    public ModifyMapRemoveCommand(Field field, MapValue value) {
        super("remove");

        String entryName = field.getName();
        for (ArgumentDefinition<?> definition : ((List<ArgumentDefinition<?>>) value.argumentDefinitionsForRemove())) {
            argument(builder -> {
                definition.applyArgument(builder);

                builder.execute(ctx -> {
                    Object k;
                    try {
                        k = definition.mapArgument(ctx);
                    } catch (InvalidArgumentException e) {
                        e.toIncorrectArgumentInputException()
                         .sendMessage(ctx);
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
}
