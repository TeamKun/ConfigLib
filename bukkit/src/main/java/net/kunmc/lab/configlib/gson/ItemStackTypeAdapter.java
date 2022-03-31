package net.kunmc.lab.configlib.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Map;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
    private static final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, ItemStack itemStack) throws IOException {
        out.value(gson.toJson(itemStack.serialize()));
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        Map<String, Object> map = gson.fromJson(in.nextString(), new TypeToken<Map<String, Object>>() {
        }.getType());
        ItemStack itemStack = ItemStack.deserialize(map);

        Map<String, Object> metaMap = ((Map<String, Object>) map.get("meta"));
        if (metaMap != null) {
            ItemMeta meta = ((ItemMeta) ConfigurationSerialization.deserializeObject((metaMap)));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }
}
