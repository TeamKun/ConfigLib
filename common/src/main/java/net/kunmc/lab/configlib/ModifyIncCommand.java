package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

import java.lang.reflect.Field;

class ModifyIncCommand extends Command {
    private final Field field;
    private final ConfigSchemaEntry<?> schemaEntry;
    private final NumericValue value;
    private final CommonBaseConfig config;

    public ModifyIncCommand(CommonBaseConfig config,
                            Field field,
                            ConfigSchemaEntry<?> schemaEntry,
                            NumericValue value) {
        super("inc");

        this.config = config;
        this.field = field;
        this.schemaEntry = schemaEntry;
        this.value = value;

        addPrerequisite(value::checkExecutable);
        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("incValue")).execute(this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        String entryName = value.resolveEntryName(field.getName());

        if (value.compare(value.max.doubleValue() - amount) > 0) {
            amount = value.max.doubleValue() - ((Number) value.value).doubleValue();
        }

        Number newValue = value.copyAdd(amount);
        try {
            ConfigSchemaValidation.validate(schemaEntry, newValue);
        } catch (InvalidValueException e) {
            e.sendMessage(ctx);
            return;
        }

        config.mutate(() -> {
            value.dispatchModifyCommand(newValue);
            value.value(newValue);
        });

        ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(entryName, ctx)));
    }
}
