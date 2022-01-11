package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.value.SingleValue;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

class ModifySetCommand extends AccessibleCommand {
    public static void register(BaseConfig config, AccessibleCommand parent) {
        for (Field field : ConfigUtil.getSingleValueFields(config)) {
            parent.appendChild(new Command(field.getName()) {
                {
                    try {
                        SingleValue<?> v = ((SingleValue<?>) field.get(config));
                        if (v.writableByCommand()) {
                            children(new ModifySetCommand(field, v, config));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private final Field field;
    private final SingleValue value;
    private final BaseConfig config;

    private ModifySetCommand(Field field, SingleValue value, BaseConfig config) {
        super("set");

        this.field = field;
        this.value = value;
        this.config = config;

        usage(value::appendArgument);
    }

    @Override
    public void execute(CommandContext ctx) {
        String entryName = field.getName();

        List<Object> argument = ctx.getTypedArgs();
        CommandSender sender = ctx.getSender();
        if (!value.isCorrectArgument(argument, sender)) {
            ctx.fail(value.incorrectArgumentMessage(argument));
            return;
        }

        Object newValue = value.argumentToValue(argument, sender);
        if (!value.validateOnSet(newValue)) {
            ctx.fail(value.invalidValueMessage(entryName, newValue));
            return;
        }

        value.onSetValue(newValue);
        value.value(newValue);
        ctx.success(value.succeedSetMessage(entryName));

        config.saveConfigIfPresent();
    }
}
