package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class LocationSetValue extends SetValue<Location> {
    public LocationSetValue(Location... locations) {
        this(Sets.newHashSet(locations));
    }

    public LocationSetValue(@NotNull Set<Location> value) {
        super(value);
    }

    @Override
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    public boolean isCorrectArgumentForAdd(List<Object> argument, CommandSender sender) {
        return true;
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
    public String invalidValueMessageForAdd(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return locationToString(l) + "はすでに" + entryName + "に追加されている座標です.";
    }

    @Override
    public String succeedMessageForAdd(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return entryName + "に" + locationToString(l) + "を追加しました.";
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
        double x = ((Double) argument.get(0));
        double y = ((Double) argument.get(1));
        double z = ((Double) argument.get(2));

        return value.stream()
                .filter(l -> l.getX() == x)
                .filter(l -> l.getY() == y)
                .anyMatch(l -> l.getZ() == z);
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
    public String invalidValueMessageForRemove(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return locationToString(l) + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageForRemove(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return entryName + "から" + locationToString(l) + "を削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "からすべての座標を削除しました.";
    }

    @Override
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream()
                .map(this::locationToString)
                .collect(Collectors.joining(", ")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    private String locationToString(Location l) {
        return String.format("{world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f}",
                l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
    }
}
