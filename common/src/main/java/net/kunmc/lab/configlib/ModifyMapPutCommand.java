package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(Field field, MapValue value) {
        super("put");

        String entryName = field.getName();
        for (MapValue.PutArgumentDefinition<?, ?> definition : ((List<MapValue.PutArgumentDefinition<?, ?>>) value.argumentDefinitionsForPut())) {
            argument(builder -> {
                ArgumentDefinition<?> keyDefinition = definition.keyDefinition();
                ArgumentDefinition<?> valueDefinition = definition.valueDefinition();
                keyDefinition.applyArgument(builder);
                valueDefinition.applyArgument(builder);

                builder.execute(ctx -> {
                    Object k;
                    Object v;
                    try {
                        k = keyDefinition.mapArgument(ctx);
                        v = valueDefinition.mapArgument(ctx);
                    } catch (InvalidArgumentException e) {
                        e.toIncorrectArgumentInputException()
                         .sendMessage(ctx);
                        return;
                    }

                    try {
                        Map map = new HashMap<>();
                        map.put(k, v);
                        value.validate(map);
                    } catch (InvalidValueException e) {
                        e.getMessages()
                         .forEach(ctx::sendFailure);
                        return;
                    }

                    value.onPutValue(k, v);
                    value.put(k, v);

                    ctx.sendSuccess(value.succeedMessageForPut(entryName, k, v));
                });
            });
        }
    }
}
