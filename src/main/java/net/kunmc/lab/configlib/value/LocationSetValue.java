package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.argument.LocationArgument;
import net.kunmc.lab.configlib.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
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
    public void appendArgumentForAdd(UsageBuilder builder) {
        CommandUtil.getArguments(builder).add(new LocationArgument("location"));
    }

    @Override
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForAdd(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForAdd(List<Object> argument, CommandSender sender) {
        Location l = ((Location) argument.get(0));

        if (sender instanceof ConsoleCommandSender) {
            l.setWorld(Bukkit.getWorlds().get(0));
        }

        if (sender instanceof Player) {
            Player p = ((Player) sender);
            l.setWorld(p.getWorld());
            l.setPitch(p.getLocation().getPitch());
            l.setYaw(p.getLocation().getYaw());
        }

        if (sender instanceof BlockCommandSender) {
            World w = ((BlockCommandSender) sender).getBlock().getWorld();
            l.setWorld(w);
        }

        return Sets.newHashSet(l);
    }

    @Override
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.doubleArgument("x", suggestionBuilder -> {
                    suggestionBuilder.suggestAll(value.stream()
                            .map(Location::getX)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                }, null)
                .doubleArgument("y", suggestionBuilder -> {
                    double x = ((double) suggestionBuilder.getTypedArgs().get(0));
                    suggestionBuilder.suggestAll(value.stream()
                            .filter(l -> l.getX() == x)
                            .map(Location::getY)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                }, null)
                .doubleArgument("z", suggestionBuilder -> {
                    double x = ((double) suggestionBuilder.getTypedArgs().get(0));
                    double y = ((double) suggestionBuilder.getTypedArgs().get(1));
                    suggestionBuilder.suggestAll(value.stream()
                            .filter(l -> l.getX() == x && l.getY() == y)
                            .map(Location::getZ)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                }, null);
    }

    @Override
    public boolean isCorrectArgumentForRemove(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessageForRemove(List<Object> argument) {
        return argument + "は不正な引数です.";
    }

    @Override
    public Set<Location> argumentToValueForRemove(List<Object> argument, CommandSender sender) {
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
                l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
    }
}
