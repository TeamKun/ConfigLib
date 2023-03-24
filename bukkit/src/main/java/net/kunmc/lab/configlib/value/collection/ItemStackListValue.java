package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.gson.ItemStackTypeAdapter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackListValue extends ListValue<ItemStack, ItemStackListValue> {
    private static final Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class,
                                                                                    new ItemStackTypeAdapter())
                                                      .create();

    public ItemStackListValue() {
        super(new ArrayList<>());
    }

    public ItemStackListValue(List<ItemStack> value) {
        super(value);
    }

    @Override
    protected void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.itemStackArgument("item")
               .integerArgument("amount", 1, Integer.MAX_VALUE);
    }

    @Override
    protected boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected List<ItemStack> argumentToValueForAdd(String entryName, List<Object> argument, CommandContext ctx) {
        ItemStack item = ((ItemStack) argument.get(0));
        item.setAmount(((Integer) argument.get(1)));
        System.out.println(item.getItemMeta());
        return Lists.newArrayList(item);
    }

    @Override
    protected void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.customArgument(new StringArgument("item", option -> {
            option.displayDefaultSuggestions(false)
                  .suggestionAction(sb -> {
                      value.stream()
                           .map(gson::toJson)
                           .forEach(sb::suggest);
                  })
                  .filter(x -> value.stream()
                                    .map(gson::toJson)
                                    .anyMatch(x::equals));
        }, StringArgument.Type.PHRASE));
    }

    @Override
    protected boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected List<ItemStack> argumentToValueForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return Lists.newArrayList(gson.fromJson(((String) argument.get(0)), ItemStack.class));
    }

    @Override
    protected String elementToString(ItemStack itemStack) {
        return gson.toJson(itemStack);
    }
}
