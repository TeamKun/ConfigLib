package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;

import java.lang.reflect.Field;

class ModifyIncCommand extends Command {
    private final Field field;
    private final NumericValue value;
    private final CommonBaseConfig config;

    public ModifyIncCommand(Field field, NumericValue value, CommonBaseConfig config) {
        super("inc");

        this.field = field;
        this.value = value;
        this.config = config;

        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("incValue"), this::exec);
    }

    public void exec(double amount, CommandContext ctx) {
        String entryName = field.getName();

        if (value.compare(value.max.doubleValue() - amount) > 0) {
            amount = value.max.doubleValue() - ((Number) value.value).doubleValue();
        }

        Number newValue = value.copyAdd(amount);
        if (value.onModifyValue(newValue, ctx)) {
            return;
        }

        value.value(newValue);
        ctx.sendSuccess(value.succeedModifyMessage(entryName));

        config.saveConfigIfPresent();
    }
}
