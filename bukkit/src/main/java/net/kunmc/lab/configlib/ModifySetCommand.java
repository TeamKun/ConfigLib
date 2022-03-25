package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

class ModifySetCommand extends Command {
    public ModifySetCommand(Field field, SingleValue value, BaseConfig config) {
        super("set");

        argument(builder -> {
            value.appendArgument(builder);

            builder.execute(ctx -> {
                String entryName = field.getName();

                List<Object> argument = ctx.getParsedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectArgument(entryName, sender, argument)) {
                    ctx.sendFailure(value.incorrectArgumentMessage(entryName, argument, sender));
                    return;
                }

                Object newValue = value.argumentToValue(argument, sender);
                if (!value.validateOnSet(entryName, newValue, sender)) {
                    ctx.sendFailure(value.invalidValueMessage(entryName, newValue, sender));
                    return;
                }

                if (value.onModifyValue(newValue, ctx)) {
                    return;
                }

                value.value(newValue);
                ctx.sendSuccess(value.succeedModifyMessage(entryName));

                config.saveConfigIfPresent();
            });
        });
    }
}
