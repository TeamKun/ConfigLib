package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;

import java.lang.reflect.Field;

class ModifyIncCommand extends Command {
    private final Field field;
    private final NumericValue value;
    private final BaseConfig config;

    public ModifyIncCommand(Field field, NumericValue value, BaseConfig config) {
        super("inc");

        this.field = field;
        this.value = value;
        this.config = config;

        argument(builder -> {
            builder.doubleArgument("incValue", -Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }

    @Override
    public void execute(CommandContext ctx) {
        String entryName = field.getName();

        double amount = 1.0;
        if (!ctx.getParsedArgs().isEmpty()) {
            amount = ((Double) ctx.getParsedArgs().get(0));
        }

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