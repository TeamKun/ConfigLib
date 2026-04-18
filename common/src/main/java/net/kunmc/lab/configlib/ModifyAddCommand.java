package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.Command;
import net.kunmc.lab.commandlib.exception.ArgumentValidationException;
import net.kunmc.lab.configlib.command.CollectionValueAddCommandMessageParameter;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import net.kunmc.lab.configlib.util.function.ArgumentApplier;
import net.kunmc.lab.configlib.util.function.ArgumentMapper;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyAddCommand extends Command {
    public ModifyAddCommand(CommonBaseConfig config, Field field, CollectionValue value) {
        super("add");

        String entryName = value.resolveEntryName(field.getName());
        for (Object definition : value.argumentDefinitionsForAdd()) {
            argument(builder -> {
                ((ArgumentApplier) definition).applyArgument(builder);

                addPrerequisite(value::checkExecutable);

                builder.execute(ctx -> {
                    Collection newValue;
                    try {
                        newValue = ((ArgumentMapper<Collection>) definition).mapArgument(ctx);
                    } catch (ArgumentValidationException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    try {
                        Collection result = value.toAdded(newValue.toArray());
                        value.validate(result);
                    } catch (InvalidValueException e) {
                        e.sendMessage(ctx);
                        return;
                    }

                    config.mutate(() -> {
                        value.dispatchAdd(newValue);
                        ((Collection) value.value()).addAll(newValue);
                    });

                    ctx.sendSuccess(value.succeedMessageForAdd(new CollectionValueAddCommandMessageParameter<>(entryName,
                                                                                                               ctx,
                                                                                                               newValue)));
                });
            });
        }
    }
}
