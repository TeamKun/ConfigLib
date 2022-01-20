package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.command.AccessibleCommand;

import java.lang.reflect.Field;

class ModifyIncCommand extends AccessibleCommand {
    private final Field field;
    private final NumericValue value;
    private final BaseConfig config;

    public ModifyIncCommand(Field field, NumericValue value, BaseConfig config) {
        super("inc");

        this.field = field;
        this.value = value;
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        String entryName = field.getName();

        double amount = 1.0;
        if (value.compareTo(value.max().doubleValue() - amount) > 0) {
            amount = value.max().doubleValue() - ((Number) value.value).doubleValue();
        }

        value.add(amount);
        if (value.onModifyValue(value.value, ctx)) {
            value.sub(amount);
            return;
        }

        ctx.success(value.succeedModifyMessage(entryName));

        config.saveConfigIfPresent();
    }
}