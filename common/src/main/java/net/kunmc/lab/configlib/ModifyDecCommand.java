package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;

class ModifyDecCommand extends Command {
    private final ConfigSchemaEntry<?> schemaEntry;
    private final NumericValue value;
    private final CommonBaseConfig config;

    public ModifyDecCommand(CommonBaseConfig config, ConfigSchemaEntry<?> schemaEntry, NumericValue value) {
        super("dec");

        this.config = config;
        this.schemaEntry = schemaEntry;
        this.value = value;

        addPrerequisite(value::checkExecutable);
        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("decValue")).execute(this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        if (value.compare(value.min.doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min.doubleValue();
        }

        Number newValue = value.copySub(amount);
        try {
            ConfigSchemaValidation.validate(schemaEntry, newValue);
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx);
            return;
        }

        try {
            config.mutate(() -> {
                value.dispatchModifyCommand(newValue);
                value.value(newValue);
            });
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx);
            return;
        }

        ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(schemaEntry.entryName(),
                                                                                                ctx)));
    }
}
