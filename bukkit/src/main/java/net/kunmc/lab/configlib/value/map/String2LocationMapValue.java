package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class String2LocationMapValue extends String2ObjectMapValue<Location, String2LocationMapValue> {
    public String2LocationMapValue() {
        this(new HashMap<>());
    }

    public String2LocationMapValue(Map<String, Location> value) {
        super(value);
    }

    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected Location argumentToValueForPut(List<Object> argument, CommandContext ctx) {
        return ((Location) argument.get(1));
    }

    @Override
    protected String valueToString(Location location) {
        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                             location.getWorld()
                                     .getName(),
                             location.getX(),
                             location.getY(),
                             location.getZ(),
                             location.getPitch(),
                             location.getYaw());
    }
}
