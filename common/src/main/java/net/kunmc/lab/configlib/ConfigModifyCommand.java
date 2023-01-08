package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.util.ChatColorUtil;
import net.kunmc.lab.configlib.util.ConfigUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

class ConfigModifyCommand extends Command {
    public ConfigModifyCommand(Set<CommonBaseConfig> configs) {
        super(SubCommandType.Modify.name);

        if (configs.isEmpty()) {
            throw new IllegalArgumentException("configs is empty");
        }

        if (configs.size() == 1) {
            configs.forEach(config -> init(config, this));
        } else {
            configs.forEach(config -> addChildren(new Command(config.entryName()) {{
                init(config, this);
            }}));
        }
    }

    private static void init(CommonBaseConfig config, Command command) {
        for (Field field : ConfigUtil.getSingleValueFields(config)) {
            SingleValue<?, ?> v;
            try {
                v = ((SingleValue<?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.writableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                execute(ctx -> {
                    ctx.sendMessageWithOption(field.getName() + ": " + v.asString(ctx),
                                              option -> option.rgb(ChatColorUtil.GREEN.getRGB())
                                                              .hoverText(v.description()));
                });
                applySet(this, field, v);
                addChildren(new Command("set") {{
                    applySet(this, field, v);
                }});

                if (v instanceof NumericValue) {
                    addChildren(new ModifyIncCommand(field, ((NumericValue<?, ?>) v)));
                    addChildren(new ModifyDecCommand(field, ((NumericValue<?, ?>) v)));
                }
            }});
        }

        for (Field field : ConfigUtil.getCollectionValueFields(config)) {
            CollectionValue<?, ?, ?> v;
            try {
                v = ((CollectionValue<?, ?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.addableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                if (v.addableByCommand()) {
                    addChildren(new ModifyAddCommand(field, v));
                }
                if (v.removableByCommand()) {
                    addChildren(new ModifyRemoveCommand(field, v));
                }
                if (v.clearableByCommand()) {
                    addChildren(new ModifyClearCommand(field, v));
                }
            }});
        }

        for (Field field : ConfigUtil.getMapValueFields(config)) {
            MapValue<?, ?, ?> v;
            try {
                v = ((MapValue<?, ?, ?>) field.get(config));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (!v.puttableByCommand() && !v.removableByCommand() && !v.clearableByCommand()) {
                continue;
            }

            command.addChildren(new Command(field.getName()) {{
                if (v.puttableByCommand()) {
                    addChildren(new ModifyMapPutCommand(field, v));
                }
                if (v.removableByCommand()) {
                    addChildren(new ModifyMapRemoveCommand(field, v));
                }
                if (v.clearableByCommand()) {
                    addChildren(new ModifyMapClearCommand(field, v));
                }
            }});
        }
    }

    private static void applySet(Command command, Field field, SingleValue value) {
        command.argument(builder -> {
            value.appendArgument(builder);

            builder.execute(ctx -> {
                String entryName = field.getName();

                List<Object> argument = ctx.getParsedArgs();
                if (!value.isCorrectArgument(entryName, argument, ctx)) {
                    ctx.sendFailure(value.incorrectArgumentMessage(entryName, argument, ctx));
                    return;
                }

                Object newValue = value.argumentToValue(argument, ctx);
                if (!value.validateOnSet(entryName, newValue, ctx)) {
                    ctx.sendFailure(value.invalidValueMessage(entryName, newValue, ctx));
                    return;
                }
                value.onModifyValueCommand(newValue);

                value.value(newValue);
                ctx.sendSuccess(value.succeedModifyMessage(entryName));
            });
        });
    }
}
