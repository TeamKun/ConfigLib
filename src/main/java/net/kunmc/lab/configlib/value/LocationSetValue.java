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
    public String invalidValueMessageForAdd(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return l + "はすでに" + entryName + "に追加されている座標です.";
    }

    @Override
    public String succeedMessageForAdd(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return entryName + "に" + l + "を追加しました.";
    }

    @Override
    public String invalidValueMessageForRemove(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return l + "は" + entryName + "に追加されていませんでした.";
    }

    @Override
    public String succeedMessageForRemove(String entryName, Set<Location> element) {
        Location l = element.toArray(new Location[0])[0];
        return entryName + "から" + l + "を削除しました.";
    }

    @Override
    public String clearMessage(String entryName) {
        return entryName + "からすべての座標を削除しました.";
    }

    @Override
    public void appendArgumentForAdd(UsageBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    public void appendArgumentForRemove(UsageBuilder builder) {
        builder.doubleArgument("x", suggestionBuilder -> {
                    suggestionBuilder.suggestAll(value.stream()
                            .map(Location::getX)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                })
                .doubleArgument("y", suggestionBuilder -> {
                    double x = ((double) suggestionBuilder.getTypedArgs().get(0));
                    suggestionBuilder.suggestAll(value.stream()
                            .filter(l -> l.getX() == x)
                            .map(Location::getY)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                })
                .doubleArgument("z", suggestionBuilder -> {
                    double x = ((double) suggestionBuilder.getTypedArgs().get(0));
                    double y = ((double) suggestionBuilder.getTypedArgs().get(1));
                    suggestionBuilder.suggestAll(value.stream()
                            .filter(l -> l.getX() == x && l.getY() == y)
                            .map(Location::getZ)
                            .map(Object::toString)
                            .collect(Collectors.toList()));
                });
    }

    @Override
    public boolean isCorrectArgumentForAdd(Object argument, CommandSender sender) {
        return true;
    }

    @Override
    public boolean isCorrectArgumentForRemove(Object argument, CommandSender sender) {
        return true;
    }

    @Override
    public Set<Location> argumentToValueForAdd(Object argument, CommandSender sender) {
        Location l = ((Location) argument);

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
    public Set<Location> argumentToValueForRemove(Object argument, CommandSender sender) {
        Location l = ((Location) argument);

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
    public void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(this.stream()
                .map(l -> String.format("{world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f}",
                        l.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw()))
                .collect(Collectors.joining(", ")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }
}
