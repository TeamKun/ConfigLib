package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.config.BaseConfig;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyRemoveCommand extends CollectionValueItem {
    public ModifyRemoveCommand(Field field, CollectionValue value, BaseConfig config) {
        super("remove", field, value, config);
    }

    @Override
    void appendArgument(UsageBuilder builder) {
        value.appendArgumentForRemove(builder);
    }

    @Override
    boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return value.isCorrectArgumentForRemove(argument, sender);
    }

    @Override
    String incorrectArgumentMessage(List<Object> argument) {
        return value.incorrectArgumentMessageForRemove(argument);
    }

    @Override
    Collection argumentToValue(List<Object> argument, CommandSender sender) {
        return value.argumentToValueForRemove(argument, sender);
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
        if (this.value.onRemoveValue(value, ctx)) {
            return;
        }
       
        ((Collection) this.value.value()).removeAll(value);
        ctx.success(this.value.succeedMessageForRemove(entryName, value));
    }
}
