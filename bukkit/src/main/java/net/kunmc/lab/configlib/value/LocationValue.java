package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import java.util.List;

public class LocationValue extends SingleValue<Location, LocationValue> {
    public LocationValue() {
        this(null);
    }

    public LocationValue(Location value) {
        super(value);
    }

    public Block getBlock() {
        if (value == null) {
            return null;
        }

        return value.getBlock();
    }

    public Vector toVector() {
        if (value == null) {
            return null;
        }

        return value.toVector();
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Location argumentToValue(List<Object> argument, CommandSender sender) {
        return (Location) argument.get(0);
    }

    @Override
    protected boolean validateOnSet(String entryName, Location newValue, CommandSender sender) {
        return !newValue.equals(value);
    }

    @Override
    protected String invalidValueMessage(String entryName, Location newValue, CommandSender sender) {
        return valueToString(newValue) + "はすでに設定されている値です.";
    }

    @Override
    protected String valueToString(Location location) {
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
