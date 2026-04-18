package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

class ConfigFieldCommand extends Command {
    ConfigFieldCommand(CommonBaseConfig config,
                       String entryName,
                       Field field,
                       Object obj,
                       boolean getEnabled,
                       boolean modifyEnabled) {
        super(entryName);

        if (obj instanceof SingleValue) {
            initSingleValue(config, field, (SingleValue<?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof CollectionValue) {
            initCollectionValue(config, field, (CollectionValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof MapValue) {
            initMapValue(config, field, (MapValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (getEnabled) {
            execute(ctx -> config.inspect(() -> ctx.sendSuccess(field.getName() + ": " + obj)));
        }
    }

    static void applySet(CommonBaseConfig config, Command command, Field field, SingleValue value) {
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
                        value.validate(newValue);
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
            applySet(config, this, field, v);
            addChildren(new Command("set") {{
                applySet(config, this, field, v);
            }});

            if (v instanceof NumericValue) {
                addChildren(new ModifyIncCommand(config, field, (NumericValue<?, ?>) v));
                addChildren(new ModifyDecCommand(config, field, (NumericValue<?, ?>) v));
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
                addChildren(new ModifyAddCommand(config, field, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyRemoveCommand(config, field, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyClearCommand(config, field, v));
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
                addChildren(new ModifyMapPutCommand(config, field, v));
            }
            if (v.isRemoveEnabled()) {
                addChildren(new ModifyMapRemoveCommand(config, field, v));
            }
            if (v.isClearEnabled()) {
                addChildren(new ModifyMapClearCommand(config, field, v));
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
