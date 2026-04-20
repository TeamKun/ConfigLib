package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.apache.commons.lang3.StringUtils;

class ConfigFieldCommand extends Command {
    ConfigFieldCommand(CommonBaseConfig config,
                       String entryName,
                       ConfigSchemaEntry<?> schemaEntry,
                       boolean getEnabled,
                       boolean modifyEnabled) {
        super(entryName);
        Object obj = schemaEntry.commandObject();

        if (obj instanceof SingleValue) {
            initSingleValue(config, schemaEntry, (SingleValue<?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof CollectionValue) {
            initCollectionValue(config, schemaEntry, (CollectionValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof MapValue) {
            initMapValue(config, schemaEntry, (MapValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (getEnabled) {
            execute(ctx -> config.inspect(() -> {
                ctx.sendSuccess(schemaEntry.entryName() + ": " + schemaEntry.displayString());
            }));
        }
    }

    static void applySet(CommonBaseConfig config,
                         Command command,
                         ConfigSchemaEntry<?> schemaEntry,
                         SingleValue value) {
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
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        config.mutate(() -> {
                            value.dispatchModifyCommand(newValue);
                            value.value(newValue);
                        });
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(schemaEntry.entryName(),
                                                                                                            ctx)));
                });
            });
        }
    }

    private void initSingleValue(CommonBaseConfig config,
                                 ConfigSchemaEntry<?> schemaEntry,
                                 SingleValue<?, ?> v,
                                 boolean getEnabled,
                                 boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(schemaEntry.entryName() + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  schemaEntry.metadata()
                                                                                                             .description())))));
        }

        if (modifyEnabled && v.isModifyEnabled()) {
            applySet(config, this, schemaEntry, v);
            addChildren(new Command("set") {{
                applySet(config, this, schemaEntry, v);
            }});

            if (v instanceof NumericValue) {
                addChildren(new ModifyIncCommand(config, schemaEntry, (NumericValue<?, ?>) v));
                addChildren(new ModifyDecCommand(config, schemaEntry, (NumericValue<?, ?>) v));
            }

            String entryName = schemaEntry.entryName();
            addChildren(new Command("reset") {{
                execute(ctx -> {
                    try {
                        config.mutate(v::resetToDefault);
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }
                    ctx.sendSuccess(entryName + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }

    private void initCollectionValue(CommonBaseConfig config,
                                     ConfigSchemaEntry<?> schemaEntry,
                                     CollectionValue<?, ?, ?> v,
                                     boolean getEnabled,
                                     boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(schemaEntry.entryName() + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  schemaEntry.metadata()
                                                                                                             .description())))));
        }

        if (modifyEnabled) {
            if (v.isAddEnabled()) {
                addChildren(new ModifyAddCommand(config, schemaEntry, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyRemoveCommand(config, schemaEntry, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyClearCommand(config, schemaEntry, v));
            }

            addChildren(new Command("reset") {{
                execute(ctx -> {
                    try {
                        config.mutate(v::resetToDefault);
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }
                    ctx.sendSuccess(schemaEntry.entryName() + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }

    private void initMapValue(CommonBaseConfig config,
                              ConfigSchemaEntry<?> schemaEntry,
                              MapValue<?, ?, ?> v,
                              boolean getEnabled,
                              boolean modifyEnabled) {
        addPrerequisite(v::checkExecutable);

        if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendMessageWithOption(schemaEntry.entryName() + ": " + v.displayString(),
                                                                          option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                                          .hoverText(StringUtils.defaultString(
                                                                                                  schemaEntry.metadata()
                                                                                                             .description())))));
        }

        if (modifyEnabled) {
            if (v.isPutEnabled()) {
                addChildren(new ModifyMapPutCommand(config, schemaEntry, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyMapRemoveCommand(config, schemaEntry, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyMapClearCommand(config, schemaEntry, v));
            }

            addChildren(new Command("reset") {{
                execute(ctx -> {
                    try {
                        config.mutate(v::resetToDefault);
                    } catch (ConfigValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }
                    ctx.sendSuccess(schemaEntry.entryName() + "をデフォルト値(" + v.displayString() + ")にリセットしました");
                });
            }});
        }
    }
}
