package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.command.SingleValue;
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
import java.util.function.Consumer;

public class LocationValue extends SingleValue<Location> {
    private transient final Consumer<Location> consumer;
    private transient boolean listable = true;
    private transient boolean writable = true;

    public LocationValue() {
        this((Location) null);
    }

    public LocationValue(Location value) {
        this(value, x -> {
        });
    }

    public LocationValue(Consumer<Location> onSet) {
        this(null, onSet);
    }

    public LocationValue(Location value, Consumer<Location> onSet) {
        super(value);
        consumer = onSet;
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
    protected boolean validateOnSet(Location newValue) {
        return !newValue.equals(value);
    }

    @Override
    protected void onSetValue(Location newValue) {
        consumer.accept(newValue);
    }

    public LocationValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
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
    public Location value() {
        return value;
    }

    @Override
    public void value(Location value) {
        this.value = value;
    }

    public LocationValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    @Override
    protected String succeedSetMessage(String entryName) {
        return entryName + "の値を" + locationToString() + "に設定しました.";
    }

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        if (value == null) {
            ctx.success(entryName + ": null");
        } else {
            ctx.success(entryName + ": " + locationToString());
        }
    }

    private String locationToString() {
        return String.format("world=%s,x=%.1f,y=%.1f,z=%.1f,pitch=%.1f,yaw=%.1f",
                value.getWorld().getName(), value.getX(), value.getY(), value.getZ(), value.getPitch(), value.getYaw());
    }
}