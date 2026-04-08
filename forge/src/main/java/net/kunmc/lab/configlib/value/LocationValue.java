package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.LocationArgument;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.ArgumentDefinition;
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
    protected Location copyValue(Location value) {
        return value.clone();
    }

    @Override
    protected List<ArgumentDefinition<Location>> argumentDefinitions() {
        return List.of(new ArgumentDefinition<>(new LocationArgument("location"), (loc, ctx) -> loc));
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
