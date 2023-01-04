package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;

import java.lang.reflect.Field;

class ModifyDecCommand extends Command {
    private final Field field;
    private final NumericValue value;
    private final CommonBaseConfig config;

    public ModifyDecCommand(Field field, NumericValue value, CommonBaseConfig config) {
        super("dec");

        this.field = field;
        this.value = value;
        this.config = config;

        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("decValue"), this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        String entryName = field.getName();

        if (value.compare(value.min.doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min.doubleValue();
        }

        Number newValue = value.copySub(amount);
        value.onModifyValue(newValue);

        value.value(newValue);
        ctx.sendSuccess(value.succeedModifyMessage(entryName));

        config.saveConfigIfPresent();
    }
}
