package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.block.BlockState;

import java.util.List;

public class LocationValue extends SingleValue<Location, LocationValue> {
    public LocationValue() {
        this(null);
    }

    public LocationValue(Location value) {
        super(value);
    }

    public BlockState getBlockState() {
        if (value == null) {
            return null;
        }

        return value.getBlockState();
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected Location argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((Location) argument.get(0));
    }

    @Override
    protected String valueToString(Location location) {
        String worldName = "null";
        if (location.getWorld() != null) {
            worldName = location.getWorld()
                                .getDimensionKey()
                                .getLocation()
                                .toString();
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
