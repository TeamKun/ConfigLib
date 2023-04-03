package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
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
    protected ItemStack argumentToValue(List<Object> argument, CommandContext ctx) {
        ItemStack item = ((ItemStack) argument.get(0));
        item.setCount(((Integer) argument.get(1)));
        return item;
    }

    @Override
    protected String valueToString(ItemStack itemStack) {
        return itemStack.toString();
    }
}
