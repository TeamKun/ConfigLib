package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.MapValuePutCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, MapValue value) {
        super("put");

        addPrerequisite(value::checkExecutable);
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
                        ConfigSchemaValidation.validate(schemaEntry, result);
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        config.mutate(() -> {
                            value.dispatchPut(k, v);
                            value.put(k, v);
                        });
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    ctx.sendSuccess(value.succeedMessageForPut(new MapValuePutCommandMessageParameter<>(schemaEntry.entryName(),
                                                                                                        ctx,
                                                                                                        k,
                                                                                                        v)));
                });
            });
        }
    }
}
