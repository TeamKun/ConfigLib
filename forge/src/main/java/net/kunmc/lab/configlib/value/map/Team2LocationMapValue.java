package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.util.Location;
import net.minecraft.command.CommandSource;

import java.util.List;

public class Team2LocationMapValue extends Team2ObjectMapValue<Location, Team2LocationMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(String entryName,
                                                         List<Object> argument,
                                                         CommandSource sender) {
        return "";
    }

    @Override
    protected Location argumentToValueForPut(List<Object> argument, CommandSource sender) {
        return ((Location) argument.get(1));
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
