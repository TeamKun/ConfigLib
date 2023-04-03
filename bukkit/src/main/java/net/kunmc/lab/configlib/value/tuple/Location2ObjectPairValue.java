package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import org.bukkit.Location;

import java.util.List;

public abstract class Location2ObjectPairValue<R, T extends Location2ObjectPairValue<R, T>> extends PairValue<Location, R, T> {
    public Location2ObjectPairValue(Location left, R right) {
        super(left, right);
    }

    @Override
    protected void appendLeftArgument(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected Location argumentToLeftValue(List<Object> argument, CommandContext ctx) {
        return ((Location) argument.get(0));
    }

    @Override
    protected String leftToString(Location location) {
        String worldName = "null";
        if (location.getWorld() != null) {
            worldName = location.getWorld()
                                .getName();
        }

        return String.format("{world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f}",
                             worldName,
                             location.getX(),
                             location.getY(),
                             location.getZ(),
                             location.getPitch(),
                             location.getYaw());
    }
}
