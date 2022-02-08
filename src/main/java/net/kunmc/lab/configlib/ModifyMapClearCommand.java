package net.kunmc.lab.configlib;

import dev.kotx.flylib.command.CommandContext;
import net.kunmc.lab.configlib.command.AccessibleCommand;

import java.lang.reflect.Field;

public class ModifyMapClearCommand extends AccessibleCommand {
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
        ctx.success(value.clearMessage(field.getName()));

        config.saveConfigIfPresent();
    }
}
