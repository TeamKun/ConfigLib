package net.kunmc.lab.command;

import dev.kotx.flylib.command.Command;
import net.kunmc.lab.config.BaseConfig;
import net.kunmc.lab.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

class ValueConfigItem extends Command {
    public ValueConfigItem(Field field, Value configValue, BaseConfig config) {
        super(field.getName());

        String entryName = field.getName();
        ArgumentType type = ArgumentType.byClass(getGenericsClass(configValue));

        usage(builder -> {
            type.appendArgument(builder);

            builder.executes(ctx -> {
                Object argument = ctx.getTypedArgs().get(0);
                if (!type.isCollectArgument(argument)) {
                    ctx.fail(argument + "は不正な引数です.");
                    return;
                }

                Object newValue = type.argumentToValue(argument);
                if (!configValue.validate(newValue)) {
                    ctx.fail(configValue.failSetMessage(entryName, newValue));
                    return;
                }

                configValue.onSetValue(newValue);
                configValue.value(newValue);
                ctx.success(configValue.succeedSetMessage(entryName));

                config.saveConfigIfPresent();
            });
        });
    }

    private static Class<?> getGenericsClass(Value value) {
        return Stream.iterate(((Object) value.getClass()), clazz -> ((Class<?>) clazz).getSuperclass())
                .filter(Objects::nonNull)
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .map(Class::getGenericInterfaces)
                .flatMap(Arrays::stream)
                .filter(ParameterizedType.class::isInstance)
                .map(ParameterizedType.class::cast)
                .map(ParameterizedType::getActualTypeArguments)
                .flatMap(Arrays::stream)
                .filter(Class.class::isInstance)
                .map(Class.class::cast)
                .findFirst().get();
    }
}
