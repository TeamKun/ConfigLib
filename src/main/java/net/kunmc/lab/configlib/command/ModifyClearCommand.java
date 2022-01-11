package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.CollectionValue;

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
        ((Collection) configValue.value()).clear();
        ctx.success(configValue.clearMessage(field.getName()));

        config.saveConfigIfPresent();
    }
}