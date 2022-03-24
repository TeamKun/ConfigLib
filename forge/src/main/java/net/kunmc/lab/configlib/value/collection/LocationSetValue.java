package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.util.Location;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationSetValue extends SetValue<Location, LocationSetValue> {
    public LocationSetValue(Location... locations) {
        this(Sets.newHashSet(locations));
    }

    public LocationSetValue(@NotNull Set<Location> value) {
        super(value);
    }

    @Override
    public void appendArgumentForAdd(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForAdd(List<Object> argument, CommandSource sender) {
        return Sets.newHashSet((Location) argument.get(0));
    }

    @Override
    public void appendArgumentForRemove(ArgumentBuilder builder) {
        builder.doubleArgument("x", sb -> {
                    value.stream()
                            .map(Location::getX)
                            .map(Object::toString)
                            .forEach(sb::suggest);
                })
                .doubleArgument("y", sb -> {
                    double x = ((double) sb.getParsedArgs().get(0));
                    value.stream()
                            .filter(l -> l.getX() == x)
                            .map(Location::getY)
                            .map(Object::toString)
                            .forEach(sb::suggest);
                })
                .doubleArgument("z", sb -> {
                    double x = ((double) sb.getParsedArgs().get(0));
                    double y = ((double) sb.getParsedArgs().get(1));
                    value.stream()
                            .filter(l -> l.getX() == x && l.getY() == y)
                            .map(Location::getZ)
                            .map(Object::toString)
                            .forEach(sb::suggest);
                });
    }

    @Override
    public boolean isCorrectArgumentForRemove(List<Object> argument, CommandSource sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForRemove(List<Object> argument, CommandSource sender) {
        double x = ((Double) argument.get(0));
        double y = ((Double) argument.get(1));
        double z = ((Double) argument.get(2));

        return value.stream()
                .filter(l -> l.getX() == x)
                .filter(l -> l.getY() == y)
                .filter(l -> l.getZ() == z)
                .collect(Collectors.toSet());
    }

    @Override
    protected String elementToString(Location location) {
        String worldName = "null";
        if (location.getWorld() != null) {
            worldName = location.getWorld().getDimensionKey().getLocation().toString();
        }

        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                worldName, location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}
