package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.MapValuePutCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ModifyMapPutCommand extends Command {
    public ModifyMapPutCommand(CommonBaseConfig config,
                               ConfigSchemaEntry<?> schemaEntry,
                               MapValue value,
                               ConfigCommandDescriptions.Provider descriptions,
                               MaskedRevealPolicy maskedRevealPolicy) {
        super("put");
        description(ConfigCommandDescriptions.put(descriptions, schemaEntry.entryName()));

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
                        e.sendMessage(ctx, descriptions);
                        return;
                    }

                    try {
                        config.mutate(() -> {
                            value.dispatchPut(k, v);
                            value.put(k, v);
                        }, ChangeTrace.command(ctx, "put " + schemaEntry.entryName(), schemaEntry.entryName()));
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx, descriptions);
                        return;
                    }

                    if (MaskedCommandOutput.shouldMask(ctx, config, schemaEntry, maskedRevealPolicy)) {
                        String masked = MaskedCommandOutput.text(ctx, config, schemaEntry, maskedRevealPolicy);
                        ctx.sendSuccess(descriptions.describe(ctx,
                                                              ConfigCommandDescriptions.Key.MAP_PUT_SUCCESS,
                                                              schemaEntry.entryName(),
                                                              masked,
                                                              masked));
                    } else {
                        ctx.sendSuccess(value.succeedMessageForPut(new MapValuePutCommandMessageParameter<>(schemaEntry.entryName(),
                                                                                                            ctx,
                                                                                                            k,
                                                                                                            v,
                                                                                                            descriptions)));
                    }
                });
            }).description(ConfigCommandDescriptions.put(descriptions, schemaEntry.entryName()));
        }
    }
}
