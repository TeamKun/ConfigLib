package net.kunmc.lab.configlib.value.collection;

import net.kunmc.lab.commandlib.argument.DoubleArgument;
import net.kunmc.lab.commandlib.argument.LocationArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.util.ListUtil;
import net.kunmc.lab.configlib.util.SetUtil;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LocationSetValue extends SetValue<Location, LocationSetValue> {
    public LocationSetValue(Location... locations) {
        this(SetUtil.newHashSet(locations));
    }

    public LocationSetValue(@NotNull Set<Location> value) {
        super(value);
    }

    @Override
    protected List<ArgumentDefinition<Set<Location>>> argumentDefinitionsForAdd() {
        return ListUtil.of(new ArgumentDefinition<>(new LocationArgument("location"),
                                                    (loc, ctx) -> SetUtil.newHashSet(loc)));
    }

    @Override
    protected List<ArgumentDefinition<Set<Location>>> argumentDefinitionsForRemove() {
        return ListUtil.of(new ArgumentDefinition<>(new DoubleArgument("x", opt -> {
            opt.suggestionAction(sb -> {
                value.stream()
                     .map(Location::getX)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), new DoubleArgument("y", opt -> {
            opt.suggestionAction(sb -> {
                double x = (Double) sb.getParsedArgs()
                                      .get(0);
                value.stream()
                     .filter(l -> l.getX() == x)
                     .map(Location::getY)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), new DoubleArgument("z", opt -> {
            opt.suggestionAction(sb -> {
                double x = (Double) sb.getParsedArgs()
                                      .get(0);
                double y = (Double) sb.getParsedArgs()
                                      .get(1);
                value.stream()
                     .filter(l -> l.getX() == x && l.getY() == y)
                     .map(Location::getZ)
                     .map(Object::toString)
                     .forEach(sb::suggest);
            });
        }), (x, y, z, ctx) -> {
            return value.stream()
                        .filter(l -> l.getX() == x && l.getY() == y && l.getZ() == z)
                        .collect(Collectors.toSet());
        }));
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
