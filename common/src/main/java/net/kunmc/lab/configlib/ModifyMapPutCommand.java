package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.MapValuePutCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(CommonBaseConfig config, Field field, MapValue value) {
        super("put");

        addPrerequisite(value::checkExecutable);

        String entryName = value.resolveEntryName(field.getName());
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
                    } catch (ArgumentValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        Map result = new HashMap<>((Map) value.value());
                        result.put(k, v);
                        value.validate(result);
                    } catch (InvalidValueException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    config.mutate(() -> {
                        value.dispatchPut(k, v);
                        value.put(k, v);
                    });

                    ctx.sendSuccess(value.succeedMessageForPut(new MapValuePutCommandMessageParameter<>(entryName,
                                                                                                        ctx,
                                                                                                        k,
                                                                                                        v)));
                });
            });
        }
    }
}
