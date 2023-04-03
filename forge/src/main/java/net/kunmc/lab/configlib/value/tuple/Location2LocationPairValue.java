package net.kunmc.lab.configlib.value.tuple;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.commandlib.util.Location;

import java.util.List;

public class Location2LocationPairValue extends Location2ObjectPairValue<Location, Location2LocationPairValue> {
    public Location2LocationPairValue(Location left, Location right) {
        super(left, right);
    }

    @Override
    protected void appendRightArgument(ArgumentBuilder builder) {
        builder.locationArgument("location2");
    }

    @Override
    protected Location argumentToRightValue(List<Object> argument, CommandContext ctx) {
        return ((Location) argument.get(1));
    }

    @Override
    protected String rightToString(Location location) {
        return leftToString(location);
    }
}
