package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.command.SingleValueModifyCommandMessageParameter;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

class ModifyIncCommand extends Command {
    private final ConfigSchemaEntry<?> schemaEntry;
    private final NumericValue value;
    private final CommonBaseConfig config;
    private final ConfigCommandDescriptions.Provider descriptions;

    public ModifyIncCommand(CommonBaseConfig config,
                            ConfigSchemaEntry<?> schemaEntry,
                            NumericValue value,
                            ConfigCommandDescriptions.Provider descriptions) {
        super("inc");
        description(ConfigCommandDescriptions.increment(descriptions, schemaEntry.entryName()));

        this.config = config;
        this.schemaEntry = schemaEntry;
        this.value = value;
        this.descriptions = descriptions;

        addPrerequisite(value::checkExecutable);
        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("incValue")).description(ConfigCommandDescriptions.incrementBy(descriptions,
                                                                                                   schemaEntry.entryName()))
                                                .execute(this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        if (value.compare(value.max.doubleValue() - amount) > 0) {
            amount = value.max.doubleValue() - ((Number) value.value).doubleValue();
        }

        Number newValue = value.copyAdd(amount);
        try {
            ConfigSchemaValidation.validate(schemaEntry, newValue);
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx, descriptions);
            return;
        }

        try {
            config.mutate(() -> {
                value.dispatchModifyCommand(newValue);
                value.value(newValue);
            }, ChangeTrace.command(ctx, "inc " + schemaEntry.entryName(), schemaEntry.entryName()));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx, descriptions);
            return;
        }

        ctx.sendSuccess(value.succeedModifyMessage(new SingleValueModifyCommandMessageParameter(schemaEntry.entryName(),
                                                                                                ctx,
                                                                                                descriptions)));
    }
}
