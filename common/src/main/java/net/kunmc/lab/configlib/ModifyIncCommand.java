package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;

class ModifyIncCommand extends Command {
    private final Field field;
    private final NumericValue value;

    public ModifyIncCommand(Field field, NumericValue value) {
        super("inc");

        this.field = field;
        this.value = value;

        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("incValue"), this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        String entryName = field.getName();

        if (value.compare(value.max.doubleValue() - amount) > 0) {
            amount = value.max.doubleValue() - ((Number) value.value).doubleValue();
        }

        Number newValue = value.copyAdd(amount);
        try {
            value.validate(newValue);
        } catch (InvalidValueException e) {
            e.getMessages()
             .forEach(ctx::sendFailure);
            return;
        }
       
        value.onModifyValueCommand(newValue);
        value.value(newValue);

        ctx.sendSuccess(value.succeedModifyMessage(entryName));
    }
}
