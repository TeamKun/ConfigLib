package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.AccessibleCommand;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.List;

public class ModifyMapRemoveCommand extends AccessibleCommand {
    public ModifyMapRemoveCommand(Field field, MapValue value, BaseConfig config) {
        super("remove");

        String entryName = field.getName();

        usage(builder -> {
            value.appendKeyArgumentForRemove(builder);

            builder.executes(ctx -> {
                List<Object> argument = ctx.getTypedArgs();
                CommandSender sender = ctx.getSender();
                if (!value.isCorrectKeyArgumentForRemove(argument, sender)) {
                    ctx.fail(value.incorrectKeyArgumentMessageForRemove(argument));
                    return;
                }

                Object k = value.argumentToKeyForRemove(argument, sender);
                if (!value.validateKeyForRemove(k)) {
                    ctx.fail(value.invalidKeyMessageForRemove(entryName, k));
                    return;
                }

                if (value.onRemoveKey(k, ctx)) {
                    return;
                }

                Object v = value.remove(k);
                ctx.success(value.succeedMessageForRemove(entryName, k, v));

                config.saveConfigIfPresent();
            });
        });
    }
}
