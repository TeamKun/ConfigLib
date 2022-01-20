package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.AccessibleCommand;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

class ModifySetCommand extends AccessibleCommand {
    public ModifySetCommand(Field field, SingleValue value, BaseConfig config) {
        super("set");

        usage(builder -> {
            value.appendArgument(builder);

            builder.executes(ctx -> {
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

                if (value.onSetValue(newValue, ctx)) {
                    return;
                }

                value.value(newValue);
                ctx.success(value.succeedSetMessage(entryName));

                config.saveConfigIfPresent();
            });
        });
    }
}
