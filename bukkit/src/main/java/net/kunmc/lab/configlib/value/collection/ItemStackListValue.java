package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kunmc.lab.commandlib.argument.IntegerArgument;
import net.kunmc.lab.commandlib.argument.ItemStackArgument;
import net.kunmc.lab.commandlib.argument.StringArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.gson.ItemStackTypeAdapter;
import net.kunmc.lab.configlib.util.ListUtil;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackListValue extends ListValue<ItemStack, ItemStackListValue> {
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ItemStack.class,
                                                                                    new ItemStackTypeAdapter())
                                                      .create();

    public ItemStackListValue() {
        super(new ArrayList<>());
    }

    public ItemStackListValue(List<ItemStack> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<List<ItemStack>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new ItemStackArgument("item"),
                                                    new IntegerArgument("amount", 1, Integer.MAX_VALUE),
                                                    (item, amount, ctx) -> {
                                                        item.setAmount(amount);
                                                        return ListUtil.of(item);
                                                    }));
    }

    @Override
    protected List<ArgumentDefinition<List<ItemStack>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new StringArgument("item", opt -> {
            opt.displayDefaultSuggestions(false)
               .suggestionAction(sb -> {
                   value.stream()
                        .map(GSON::toJson)
                        .forEach(sb::suggest);
               })
               .filter(x -> {
                   return value.stream()
                               .map(GSON::toJson)
                               .anyMatch(x::equals);
               });
        }, StringArgument.Type.PHRASE), (item, ctx) -> Lists.newArrayList(GSON.fromJson(item, ItemStack.class))));
    }

    @Override
    protected String elementToString(ItemStack itemStack) {
        return GSON.toJson(itemStack);
    }
}
