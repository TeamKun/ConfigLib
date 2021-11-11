package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.SingleValue;

import java.lang.reflect.Field;

class SingleValueConfigItem extends Command {
    public SingleValueConfigItem(Field field, SingleValue configValue, BaseConfig config) {
        super(field.getName());

        String entryName = field.getName();

        usage(builder -> {
            configValue.appendArgument(builder);

            builder.executes(ctx -> {
                Object argument = ctx.getTypedArgs().get(0);
                if (!configValue.isCorrectArgument(argument)) {
                    ctx.fail(configValue.incorrectArgumentMessage(argument));
                    return;
                }

                Object newValue = configValue.argumentToValue(argument);
                if (!configValue.validateOnSet(newValue)) {
                    ctx.fail(configValue.invalidValueMessage(entryName, newValue));
                    return;
                }

                configValue.onSetValue(newValue);
                configValue.value(newValue);
                ctx.success(configValue.succeedSetMessage(entryName));

                config.saveConfigIfPresent();
            });
        });
    }
}
