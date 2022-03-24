package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;

import java.lang.reflect.Field;

class ModifyDecCommand extends Command {
    private final Field field;
    private final NumericValue value;
    private final BaseConfig config;

    public ModifyDecCommand(Field field, NumericValue value, BaseConfig config) {
        super("dec");

        this.field = field;
        this.value = value;
        this.config = config;

        argument(builder -> {
            builder.doubleArgument("decValue", -Double.MAX_VALUE, Double.MAX_VALUE);
        });
    }

    @Override
    public void execute(CommandContext ctx) {
        String entryName = field.getName();

        double amount = 1.0;
        if (!ctx.getParsedArgs().isEmpty()) {
            amount = ((Double) ctx.getParsedArgs().get(0));
        }

        if (value.compareTo(value.min.doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min.doubleValue();
        }

        Number newValue = value.copySub(amount);
        if (value.onModifyValue(newValue, ctx)) {
            return;
        }

        value.value(newValue);
        ctx.sendSuccess(value.succeedModifyMessage(entryName));

        config.saveConfigIfPresent();
    }

}