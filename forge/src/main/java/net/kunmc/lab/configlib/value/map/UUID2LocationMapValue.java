package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.argument.LocationArgument;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;

import java.util.List;
import java.util.UUID;

public class UUID2LocationMapValue extends UUID2ObjectMapValue<Location, UUID2LocationMapValue> {
    @Override
    protected List<PutArgumentDefinition<UUID, Location>> argumentDefinitionsForPut() {
        return ListUtil.of(new PutArgumentDefinition<>(keyArgumentDefinitionForPut(),
                                                       new ArgumentDefinition<>(new LocationArgument("location"),
                                                                                (loc, ctx) -> loc)));
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

        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                             worldName,
                             location.getX(),
                             location.getY(),
                             location.getZ(),
                             location.getPitch(),
                             location.getYaw());
    }
}
