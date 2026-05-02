package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.configlib.exception.ConfigValidationException;
import net.kunmc.lab.configlib.schema.ConfigSchemaEntry;
import net.kunmc.lab.configlib.store.ChangeTrace;

class ModifyDecCommand extends Command {
    private final ConfigSchemaEntry<?> schemaEntry;
    private final NumericValue value;
    private final CommonBaseConfig config;
    private final ConfigCommandDescriptions.Provider descriptions;
    private final MaskedRevealPolicy maskedRevealPolicy;

    public ModifyDecCommand(CommonBaseConfig config,
                            ConfigSchemaEntry<?> schemaEntry,
                            NumericValue value,
                            ConfigCommandDescriptions.Provider descriptions,
                            MaskedRevealPolicy maskedRevealPolicy) {
        super("dec");
        description(ConfigCommandDescriptions.decrement(descriptions, schemaEntry.entryName()));

        this.config = config;
        this.schemaEntry = schemaEntry;
        this.value = value;
        this.descriptions = descriptions;
        this.maskedRevealPolicy = maskedRevealPolicy;

        addPrerequisite(value::checkExecutable);
        execute(ctx -> exec(1.0, ctx));
        argument(new DoubleArgument("decValue")).description(ConfigCommandDescriptions.decrementBy(descriptions,
                                                                                                   schemaEntry.entryName()))
                                                .execute(this::exec);
    }

    private void exec(double amount, CommandContext ctx) {
        if (value.compare(value.min.doubleValue() + amount) < 0) {
            amount = ((Number) value.value).doubleValue() - value.min.doubleValue();
        }

        Number newValue = value.copySub(amount);
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
            }, ChangeTrace.command(ctx, "dec " + schemaEntry.entryName(), schemaEntry.entryName()));
        } catch (ConfigValidationException e) {
            e.sendMessage(ctx, descriptions);
            return;
        }

        if (MaskedCommandOutput.shouldMask(ctx, config, schemaEntry, maskedRevealPolicy)) {
            ctx.sendSuccess(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.SINGLE_VALUE_MODIFY_SUCCESS,
                                                  schemaEntry.entryName(),
                                                  MaskedCommandOutput.text(ctx,
                                                                           config,
                                                                           schemaEntry,
                                                                           maskedRevealPolicy)));
        } else {
            ctx.sendSuccess(descriptions.describe(ctx,
                                                  ConfigCommandDescriptions.Key.SINGLE_VALUE_MODIFY_SUCCESS,
                                                  schemaEntry.entryName(),
                                                  value.valueToString(value.value())));
        }
    }
}
