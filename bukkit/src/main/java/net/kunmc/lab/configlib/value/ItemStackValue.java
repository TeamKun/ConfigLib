package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStackValue extends SingleValue<ItemStack, ItemStackValue> {
    public ItemStackValue() {
        this(null);
    }

    public ItemStackValue(ItemStack value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.itemStackArgument("item")
                .integerArgument("amount", 1, Integer.MAX_VALUE);
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return "";
    }

    @Override
    protected ItemStack argumentToValue(List<Object> argument, CommandSender sender) {
        ItemStack item = ((ItemStack) argument.get(0));
        item.setAmount(((Integer) argument.get(1)));
        return item;
    }

    @Override
    protected boolean validateOnSet(String entryName, ItemStack newValue, CommandSender sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, ItemStack newValue, CommandSender sender) {
        return "";
    }

    @Override
    protected String valueToString(ItemStack itemStack) {
        return itemStack.toString();
    }
}
