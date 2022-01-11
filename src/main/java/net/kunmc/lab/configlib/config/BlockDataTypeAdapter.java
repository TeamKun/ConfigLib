package net.kunmc.lab.configlib.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.io.IOException;

public class BlockDataTypeAdapter extends TypeAdapter<BlockData> {
    @Override
    public void write(JsonWriter out, BlockData value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.getMaterial().name());
        }
    }

    @Override
    public BlockData read(JsonReader in) throws IOException {
        String s = in.nextString();

        Material m;
        try {
            m = Material.valueOf(s);
        } catch (IllegalArgumentException e) {
            throw new IOException(s + " is invalid Material name");
        }

        if (!m.isBlock()) {
            throw new IOException("Could not create BlockData from" + s);
        }

        return m.createBlockData();
    }
}
