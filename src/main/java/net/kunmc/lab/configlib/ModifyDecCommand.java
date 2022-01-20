package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.command.AccessibleCommand;

import java.lang.reflect.Field;

class ModifyDecCommand extends AccessibleCommand {
    private final Field field;
    private final NumericValue value;
    private final BaseConfig config;

    public ModifyDecCommand(Field field, NumericValue value, BaseConfig config) {
        super("dec");

        this.field = field;
        this.value = value;
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        String entryName = field.getName();

        double amount = 1.0;
        if (value.compareTo(value.min().doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min().doubleValue();
        }

        value.sub(amount);
        if (value.onModifyValue(value.value, ctx)) {
            value.add(amount);
            return;
        }

        ctx.success(value.succeedModifyMessage(entryName));

        config.saveConfigIfPresent();
    }

}