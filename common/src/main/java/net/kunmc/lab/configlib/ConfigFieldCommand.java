package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.InvalidArgumentException;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

class ConfigFieldCommand extends Command {
    ConfigFieldCommand(String commandName, Field field, Object obj, boolean getEnabled, boolean modifyEnabled) {
        super(commandName);

        if (obj instanceof SingleValue) {
            initSingleValue(field, (SingleValue<?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof CollectionValue) {
            initCollectionValue(field, (CollectionValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (obj instanceof MapValue) {
            initMapValue(field, (MapValue<?, ?, ?>) obj, getEnabled, modifyEnabled);
        } else if (getEnabled) {
            execute(ctx -> ctx.sendSuccess(field.getName() + ": " + obj));
        }
    }

    private void initSingleValue(Field field, SingleValue<?, ?> v, boolean getEnabled, boolean modifyEnabled) {
        if (getEnabled && v.listable()) {
            execute(ctx -> ctx.sendMessageWithOption(field.getName() + ": " + v.format(),
                                                     option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                     .hoverText(StringUtils.defaultString(v.description()))));
        }

        if (modifyEnabled && v.writableByCommand()) {
            applySet(this, field, v);
            addChildren(new Command("set") {{
                applySet(this, field, v);
            }});

            if (v instanceof NumericValue) {
                addChildren(new ModifyIncCommand(field, (NumericValue<?, ?>) v));
                addChildren(new ModifyDecCommand(field, (NumericValue<?, ?>) v));
            }
        }
    }

    private void initCollectionValue(Field field,
                                     CollectionValue<?, ?, ?> v,
                                     boolean getEnabled,
                                     boolean modifyEnabled) {
        if (getEnabled && v.listable()) {
            execute(ctx -> ctx.sendMessageWithOption(field.getName() + ": " + v.format(),
                                                     option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                     .hoverText(StringUtils.defaultString(v.description()))));
        }

        if (modifyEnabled) {
            if (v.addableByCommand()) {
                addChildren(new ModifyAddCommand(field, v));
            }
            if (v.removableByCommand()) {
                addChildren(new ModifyRemoveCommand(field, v));
            }
            if (v.clearableByCommand()) {
                addChildren(new ModifyClearCommand(field, v));
            }
        }
    }

    private void initMapValue(Field field, MapValue<?, ?, ?> v, boolean getEnabled, boolean modifyEnabled) {
        if (getEnabled && v.listable()) {
            execute(ctx -> ctx.sendMessageWithOption(field.getName() + ": " + v.format(),
                                                     option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                                     .hoverText(StringUtils.defaultString(v.description()))));
        }

        if (modifyEnabled) {
            if (v.puttableByCommand()) {
                addChildren(new ModifyMapPutCommand(field, v));
            }
            if (v.removableByCommand()) {
                addChildren(new ModifyMapRemoveCommand(field, v));
            }
            if (v.clearableByCommand()) {
                addChildren(new ModifyMapClearCommand(field, v));
            }
        }
    }

    static void applySet(Command command, Field field, SingleValue value) {
        String entryName = field.getName();
        for (Object definition : value.argumentDefinitions()) {
            command.argument(builder -> {
                ((ArgumentApplier) definition).applyArgument(builder);

                builder.execute(ctx -> {
                    Object newValue;
                    try {
                        newValue = ((ArgumentMapper) definition).mapArgument(ctx);
                    } catch (InvalidArgumentException e) {
                        e.toIncorrectArgumentInputException()
                         .sendMessage(ctx);
                        return;
                    }

                    try {
                        value.validate(newValue);
                    } catch (InvalidValueException e) {
                        e.getMessages()
                         .forEach(ctx::sendFailure);
                        return;
                    }

                    value.onModifyValueCommand(newValue);
                    value.value(newValue);

                    ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(entryName,
                                                                                                            ctx)));
                });
            });
        }
    }
}
