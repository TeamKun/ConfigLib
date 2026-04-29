package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.commandlib.argument.LocationArgument;
import net.kunmc.lab.commandlib.util.Location;
import net.kunmc.lab.configlib.ArgumentDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationSetValue extends SetValue<Location, LocationSetValue> {
    public LocationSetValue(Location... locations) {
        this(Set.of(locations));
    }

    public LocationSetValue(@NotNull Set<Location> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<Set<Location>>> argumentDefinitionsForAdd() {
        return List.of(new ArgumentDefinition<>(new LocationArgument("location"), (loc, ctx) -> Set.of(loc)));
    }

    @Override
    protected List<ArgumentDefinition<Set<Location>>> argumentDefinitionsForRemove() {
        return List.of(new ArgumentDefinition<>(new DoubleArgument("x").suggestionAction(sb -> {
            value.stream()
                 .map(Location::getX)
                 .map(Object::toString)
                 .forEach(sb::suggest);
        }), new DoubleArgument("y").suggestionAction(sb -> {
            double x = (Double) sb.getArgument(0);
            value.stream()
                 .filter(l -> l.getX() == x)
                 .map(Location::getY)
                 .map(Object::toString)
                 .forEach(sb::suggest);
        }), new DoubleArgument("z").suggestionAction(sb -> {
            double x = (Double) sb.getArgument(0);
            double y = (Double) sb.getArguments(1);
            value.stream()
                 .filter(l -> l.getX() == x && l.getY() == y)
                 .map(Location::getZ)
                 .map(Object::toString)
                 .forEach(sb::suggest);
        }), (x, y, z, ctx) -> {
            return value.stream()
                        .filter(l -> l.getX() == x && l.getY() == y && l.getZ() == z)
                        .collect(Collectors.toSet());
        }));
    }

    @Override
    protected String elementToString(Location location) {
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
