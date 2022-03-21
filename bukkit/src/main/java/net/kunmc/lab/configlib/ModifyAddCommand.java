package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyAddCommand extends Command {
    public ModifyAddCommand(Field field, CollectionValue value, BaseConfig config) {
        super("add");

        String entryName = field.getName();

        usage(builder -> {
            value.appendArgumentForAdd(builder);

            builder.executes(ctx -> {
                List<Object> argument = ctx.getTypedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectArgumentForAdd(argument, sender)) {
                    ctx.fail(value.incorrectArgumentMessageForAdd(argument));
                    return;
                }

                Collection newValue = value.argumentToValueForAdd(argument, sender);
                if (!value.validateForAdd(newValue)) {
                    ctx.fail(value.invalidValueMessageForAdd(entryName, newValue));
                    return;
                }

                if (value.onAddValue(newValue, ctx)) {
                    return;
                }

                ((Collection) value.value()).addAll(newValue);
                ctx.success(value.succeedMessageForAdd(entryName, newValue));

                config.saveConfigIfPresent();
            });
        });
    }
}
