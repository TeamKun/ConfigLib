package net.kunmc.lab.configlib.value.collection;

import com.google.common.collect.Sets;
import net.kunmc.lab.commandlib.ArgumentBuilder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
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
    public boolean isCorrectArgumentForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForAdd(String entryName, List<Object> argument, CommandSender sender) {
        return Sets.newHashSet(((Location) argument.get(0)));
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
                   double x = ((double) sb.getParsedArgs()
                                          .get(0));
                   value.stream()
                        .filter(l -> l.getX() == x)
                        .map(Location::getY)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               })
               .doubleArgument("z", sb -> {
                   double x = ((double) sb.getParsedArgs()
                                          .get(0));
                   double y = ((double) sb.getParsedArgs()
                                          .get(1));
                   value.stream()
                        .filter(l -> l.getX() == x && l.getY() == y)
                        .map(Location::getZ)
                        .map(Object::toString)
                        .forEach(sb::suggest);
               });
    }

    @Override
    public boolean isCorrectArgumentForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(String entryName, List<Object> argument, CommandSender sender) {
        return argument + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForRemove(String entryName, List<Object> argument, CommandSender sender) {
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
    protected String elementToString(Location l) {
        return String.format("{world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f}",
                             l.getWorld()
                              .getName(),
                             l.getX(),
                             l.getY(),
                             l.getZ(),
                             l.getPitch(),
                             l.getYaw());
    }
}
