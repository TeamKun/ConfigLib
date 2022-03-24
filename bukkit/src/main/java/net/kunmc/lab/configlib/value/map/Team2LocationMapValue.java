package net.kunmc.lab.configlib.value.map;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Team2LocationMapValue extends Team2ObjectMapValue<Location, Team2LocationMapValue> {
    @Override
    protected void appendValueArgumentForPut(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectValueArgumentMessageForPut(List<Object> argument) {
        return "";
    }

    @Override
    protected Location argumentToValueForPut(List<Object> argument, CommandSender sender) {
        return ((Location) argument.get(1));
    }

    @Override
    protected String valueToString(Location location) {
        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}
