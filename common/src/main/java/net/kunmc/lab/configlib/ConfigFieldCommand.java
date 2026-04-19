package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

class ConfigFieldCommand extends Command {
    ConfigFieldCommand(CommonBaseConfig config,
                       String entryName,
                       Field field,
                       Object obj,
                       ConfigSchemaEntry<?> schemaEntry,
                       boolean getEnabled,
                       boolean modifyEnabled) {
        super(entryName);

        if (obj instanceof SingleValue) {
            initSingleValue(config, field, schemaEntry, (SingleValue<?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof CollectionValue) {
            initCollectionValue(config, field, schemaEntry, (CollectionValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof MapValue) {
            initMapValue(config, field, schemaEntry, (MapValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendSuccess(field.getName() + ": " + obj)));
        }
    }

    static void applySet(CommonBaseConfig config,
                         Command command,
                         Field field,
                         ConfigSchemaEntry<?> schemaEntry,
                         SingleValue value) {
        String entryName = value.resolveEntryName(field.getName());
        for (Object definition : value.argumentDefinitions()) {
            command.argument(builder -> {
                ((ArgumentApplier) definition).applyArgument(builder);

                builder.execute(ctx -> {
                    Object newValue;
                    try {
                        newValue = ((ArgumentMapper<?>) definition).mapArgument(ctx);
                    } catch (ArgumentValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        ConfigSchemaValidation.validate(schemaEntry, newValue);
                    } catch (InvalidValueException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    config.mutate(() -> {
                        value.dispatchModifyCommand(newValue);
                        value.value(newValue);
                    });

                    ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(entryName,
                                                                                                            ctx)));
                });
            });
        }
    }

    private void initSingleValue(CommonBaseConfig config,
                                 Field field,
                                 ConfigSchemaEntry<?> schemaEntry,
                                 SingleValue<?, ?> v,
                                 boolean getEnabled,
                                 boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(v.resolveEntryName(field.getName()) + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  v.description())))));
        }

        if (modifyEnabled && v.isModifyEnabled()) {
            applySet(config, this, field, schemaEntry, v);
            addChildren(new Command("set") {{
                applySet(config, this, field, schemaEntry, v);
            }});

            if (v instanceof NumericValue) {
                addChildren(new ModifyIncCommand(config, field, schemaEntry, (NumericValue<?, ?>) v));
                addChildren(new ModifyDecCommand(config, field, schemaEntry, (NumericValue<?, ?>) v));
            }

            String entryName = v.resolveEntryName(field.getName());
            addChildren(new Command("reset") {{
                execute(ctx -> {
                    config.mutate(v::resetToDefault);
                    ctx.sendSuccess(entryName + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }

    private void initCollectionValue(CommonBaseConfig config,
                                     Field field,
                                     ConfigSchemaEntry<?> schemaEntry,
                                     CollectionValue<?, ?, ?> v,
                                     boolean getEnabled,
                                     boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(v.resolveEntryName(field.getName()) + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  v.description())))));
        }

        if (modifyEnabled) {
            if (v.isAddEnabled()) {
                addChildren(new ModifyAddCommand(config, field, schemaEntry, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyRemoveCommand(config, field, schemaEntry, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyClearCommand(config, field, schemaEntry, v));
            }

            String entryName = v.resolveEntryName(field.getName());
            addChildren(new Command("reset") {{
                execute(ctx -> {
                    config.mutate(v::resetToDefault);
                    ctx.sendSuccess(entryName + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }

    private void initMapValue(CommonBaseConfig config,
                              Field field,
                              ConfigSchemaEntry<?> schemaEntry,
                              MapValue<?, ?, ?> v,
                              boolean getEnabled,
                              boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(v.resolveEntryName(field.getName()) + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  v.description())))));
        }

        if (modifyEnabled) {
            if (v.isPutEnabled()) {
                addChildren(new ModifyMapPutCommand(config, field, schemaEntry, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyMapRemoveCommand(config, field, schemaEntry, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyMapClearCommand(config, field, schemaEntry, v));
            }

            String entryName = v.resolveEntryName(field.getName());
            addChildren(new Command("reset") {{
                execute(ctx -> {
                    config.mutate(v::resetToDefault);
                    ctx.sendSuccess(entryName + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }
}
