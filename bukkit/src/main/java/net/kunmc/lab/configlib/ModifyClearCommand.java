package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.command.AccessibleCommand;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyClearCommand extends AccessibleCommand {
    private final Field field;
    private final CollectionValue configValue;
    private final BaseConfig config;

    public ModifyClearCommand(Field field, CollectionValue value, BaseConfig config) {
        super("clear");

        this.field = field;
        this.configValue = value;
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        if (configValue.onClearValue(ctx)) {
            return;
        }

        ((Collection) configValue.value()).clear();
        ctx.success(configValue.clearMessage(field.getName()));

        config.saveConfigIfPresent();
    }
}
