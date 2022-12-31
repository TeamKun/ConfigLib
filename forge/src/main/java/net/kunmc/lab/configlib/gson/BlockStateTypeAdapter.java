package net.kunmc.lab.configlib.gson;

import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.BlockState;

import java.io.IOException;

public class BlockStateTypeAdapter extends TypeAdapter<BlockState> {
    @Override
    public void write(JsonWriter out, BlockState value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.value(BlockState.CODEC.encodeStart(JsonOps.INSTANCE, value)
                                  .result()
                                  .get()
                                  .toString());
    }

    @Override
    public BlockState read(JsonReader in) throws IOException {
        String s = in.nextString();

        if (s.equals("null")) {
            return null;
        }

        return BlockState.CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(s))
                               .result()
                               .get()
                               .getFirst();
    }
}
