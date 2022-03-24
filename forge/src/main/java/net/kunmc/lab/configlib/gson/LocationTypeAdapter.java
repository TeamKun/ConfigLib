package net.kunmc.lab.configlib.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.kunmc.lab.commandlib.util.Location;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.beginObject();

            String worldName = "null";
            if (value.getWorld() != null) {
                worldName = value.getWorld().getDimensionKey().getLocation().toString();
            }
            out.name("world").value(worldName);
            out.name("x").value(value.getX());
            out.name("y").value(value.getY());
            out.name("z").value(value.getZ());
            out.name("pitch").value(value.getPitch());
            out.name("yaw").value(value.getYaw());

            out.endObject();
        }
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        in.beginObject();

        in.nextName();
        String worldName = in.nextString();
        World w = null;
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            w = ServerLifecycleHooks.getCurrentServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldName)));
        }

        in.nextName();
        double x = in.nextDouble();

        in.nextName();
        double y = in.nextDouble();

        in.nextName();
        double z = in.nextDouble();

        in.nextName();
        float pitch = ((float) in.nextDouble());

        in.nextName();
        float yaw = ((float) in.nextDouble());

        in.endObject();

        return new Location(w, x, y, z, yaw, pitch);
    }
}
