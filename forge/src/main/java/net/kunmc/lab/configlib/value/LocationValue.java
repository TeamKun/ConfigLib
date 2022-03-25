package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.SingleValue;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;

import java.util.List;

public class LocationValue extends SingleValue<Location, LocationValue> {
    public LocationValue() {
        this((Location) null);
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
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandSource sender) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Location argumentToValue(List<Object> argument, CommandSource sender) {
        return ((Location) argument.get(0));
    }

    @Override
    protected boolean validateOnSet(String entryName, Location newValue, CommandSource sender) {
        return !newValue.equals(value);
    }

    @Override
    protected String invalidValueMessage(String entryName, Location newValue, CommandSource sender) {
        return valueToString(newValue) + "はすでに設定されている値です.";
    }

    @Override
    protected String valueToString(Location location) {
        String worldName = "null";
        if (location.getWorld() != null) {
            worldName = location.getWorld().getDimensionKey().getLocation().toString();
        }

        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                worldName, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}