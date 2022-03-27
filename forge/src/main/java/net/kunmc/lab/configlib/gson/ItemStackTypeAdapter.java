package net.kunmc.lab.configlib.gson;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        out.value(ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, value).result().get().toString());
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        return ItemStack.CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(in.nextString())).result().get().getFirst();
    }
}
