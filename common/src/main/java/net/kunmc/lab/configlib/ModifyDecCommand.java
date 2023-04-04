package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.exception.InvalidValueException;

import java.lang.reflect.Field;

class ModifyDecCommand extends Command {
    private final Field field;
    private final NumericValue value;

    public ModifyDecCommand(Field field, NumericValue value) {
        super("dec");

        this.field = field;
        this.value = value;

        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("decValue"), this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        String entryName = field.getName();

        if (value.compare(value.min.doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min.doubleValue();
        }

        Number newValue = value.copySub(amount);
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
