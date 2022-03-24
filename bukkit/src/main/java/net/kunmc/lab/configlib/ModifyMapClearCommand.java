package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.CommandContext;

import java.lang.reflect.Field;

class ModifyMapClearCommand extends Command {
    private final Field field;
    private final MapValue value;
    private final BaseConfig config;

    public ModifyMapClearCommand(Field field, MapValue value, BaseConfig config) {
        super("clear");

        this.field = field;
        this.value = value;
        this.config = config;
    }

    @Override
    public void execute(CommandContext ctx) {
        if (value.onClearMap(ctx)) {
            return;
        }

        value.clear();
        ctx.sendSuccess(value.clearMessage(field.getName()));

        config.saveConfigIfPresent();
    }
}
