package net.kunmc.lab.configlib.command;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.config.BaseConfig;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

class ModifyAddCommand extends CollectionValueItem {
    public ModifyAddCommand(Field field, CollectionValue value, BaseConfig config) {
        super("add", field, value, config);
    }

    @Override
    void appendArgument(UsageBuilder builder) {
        value.appendArgumentForAdd(builder);
    }

    @Override
    boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return value.isCorrectArgumentForAdd(argument, sender);
    }

    @Override
    String incorrectArgumentMessage(List<Object> argument) {
        return value.incorrectArgumentMessageForAdd(argument);
    }

    @Override
    Collection argumentToValue(List<Object> argument, CommandSender sender) {
        return value.argumentToValueForAdd(argument, sender);
    }


    @Override
    boolean validate(Collection value) {
        return this.value.validateForAdd(value);
    }

    @Override
    String invalidMessage(String entryName, Collection value) {
        return this.value.invalidValueMessageForAdd(entryName, value);
    }

    @Override
    void writeProcess(CommandContext ctx, String entryName, Collection value) {
        ((Collection) this.value.value()).addAll(value);
        ctx.success(this.value.succeedMessageForAdd(entryName, value));
    }
}
