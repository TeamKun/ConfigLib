package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;

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
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return "";
    }

    @Override
    protected ItemStack argumentToValue(List<Object> argument, CommandSource sender) {
        ItemStack item = ((ItemStack) argument.get(0));
        item.setCount(((Integer) argument.get(1)));
        return item;
    }

    @Override
    protected boolean validateOnSet(String entryName, ItemStack newValue, CommandSource sender) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, ItemStack newValue, CommandSource sender) {
        return "";
    }

    @Override
    protected String valueToString(ItemStack itemStack) {
        return itemStack.toString();
    }
}
