package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;

import java.lang.reflect.Field;

class PrimitiveConfigItem extends Command {
    public PrimitiveConfigItem(Field field, BaseConfig config) {
        super(field.getName());

        String entryName = field.getName();
        ArgumentType type = ArgumentType.byClass(field.getType());

        usage(builder -> {
            type.appendArgument(builder);

            builder.executes(ctx -> {
                Object argument = ctx.getTypedArgs().get(0);
                if (!type.isCollectArgument(argument)) {
                    ctx.fail(argument + "は不正な引数です.");
                    return;
                }

                Object newValue = type.argumentToValue(argument);
                try {
                    field.set(config, newValue);
                    ctx.success(entryName + "の値を" + newValue + "に設定しました.");
                    config.saveConfigIfPresent();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
