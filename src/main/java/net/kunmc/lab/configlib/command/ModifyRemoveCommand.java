package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.config.BaseConfig;
import net.kunmc.lab.configlib.value.CollectionValue;

import java.lang.reflect.Field;
import java.util.Collection;

class ModifyRemoveCommand extends CollectionValueItem {
    public ModifyRemoveCommand(Field field, CollectionValue value, BaseConfig config) {
        super("remove", field, value, config);
    }

    @Override
    void appendArgument(UsageBuilder builder) {
        value.appendArgumentForRemove(builder);
    }

    @Override
    boolean isCorrectArgument(Object argument) {
        return value.isCorrectArgumentForRemove(argument);
    }

    @Override
    String incorrectArgumentMessage(Object argument) {
        return value.incorrectArgumentMessageForRemove(argument);
    }

    @Override
    Collection argumentToValue(Object argument) {
        return value.argumentToValueForRemove(argument);
    }

    @Override
    boolean validate(Collection value) {
        return this.value.validateForRemove(value);
    }

    @Override
    String invalidMessage(String entryName, Collection value) {
        return this.value.invalidValueMessageForRemove(entryName, value);
    }

    @Override
    void writeProcess(CommandContext ctx, String entryName, Collection value) {
        ((Collection) this.value.value()).removeAll(value);
        ctx.success(this.value.succeedMessageForRemove(entryName, value));
    }
}
