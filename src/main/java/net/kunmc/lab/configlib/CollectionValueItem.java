package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.command.AccessibleCommand;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

abstract class CollectionValueItem extends AccessibleCommand {
    protected final Field field;
    protected final CollectionValue value;
    protected final BaseConfig config;

    public CollectionValueItem(String operationName, Field field, CollectionValue value, BaseConfig config) {
        super(operationName);

        this.field = field;
        this.value = value;
        this.config = config;

        String entryName = field.getName();

        usage(builder -> {
            appendArgument(builder);

            builder.executes(ctx -> {
                List<Object> argument = ctx.getTypedArgs();
                CommandSender sender = ctx.getSender();
                if (!isCorrectArgument(argument, sender)) {
                    ctx.fail(incorrectArgumentMessage(argument));
                    return;
                }

                Collection newValue = argumentToValue(argument, sender);
                if (!validate(newValue)) {
                    ctx.fail(invalidMessage(entryName, newValue));
                    return;
                }

                writeProcess(ctx, entryName, newValue);

                config.saveConfigIfPresent();
            });
        });
    }

    abstract void appendArgument(UsageBuilder builder);

    abstract boolean isCorrectArgument(List<Object> argument, CommandSender sender);

    abstract String incorrectArgumentMessage(List<Object> argument);

    abstract Collection argumentToValue(List<Object> argument, CommandSender sender);

    abstract boolean validate(Collection value);

    abstract String invalidMessage(String entryName, Collection value);

    abstract void writeProcess(CommandContext ctx, String entryName, Collection value);
}
