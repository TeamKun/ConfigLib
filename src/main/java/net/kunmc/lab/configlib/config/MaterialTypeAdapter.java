package net.kunmc.lab.configlib.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;

import java.io.IOException;

public class MaterialTypeAdapter extends TypeAdapter<Material> {
    @Override
    public void write(JsonWriter jsonWriter, Material material) throws IOException {
        if (material == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(material.name());
        }
    }

    @Override
    public Material read(JsonReader jsonReader) throws IOException {
        String s = jsonReader.nextString();

        Material m;
        try {
            m = Material.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException(s + " is invalid Material name");
        }

        return m;
    }
}
