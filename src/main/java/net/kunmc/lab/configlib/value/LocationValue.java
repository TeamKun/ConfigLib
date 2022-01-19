package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class LocationValue extends SingleValue<Location> {
    private transient boolean listable = true;
    private transient boolean writable = true;

    public LocationValue() {
        this((Location) null);
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
    protected boolean writableByCommand() {
        return writable;
    }

    public LocationValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不正な引数です.";
    }

    @Override
    protected Location argumentToValue(List<Object> argument, CommandSender sender) {
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

        return l;
    }

    @Override
    protected boolean validateOnSet(Location newValue) {
        return !newValue.equals(value);
    }

    @Override
    protected String invalidValueMessage(String entryName, Location newValue) {
        return locationToString(newValue) + "はすでに設定されている値です.";
    }

    @Override
    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + locationToString(value) + "に設定しました.";
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public LocationValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        if (value == null) {
            ctx.success(entryName + ": null");
        } else {
            ctx.success(entryName + ": " + locationToString(value));
        }
    }

    private String locationToString(Location location) {
        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }
}