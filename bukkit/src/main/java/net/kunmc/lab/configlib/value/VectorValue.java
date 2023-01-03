package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.SingleValue;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public class VectorValue extends SingleValue<Vector, VectorValue> {
    public VectorValue() {
        this(null);
    }

    public VectorValue(Vector value) {
        super(value);
    }

    @Override
    protected void appendArgument(ArgumentBuilder builder) {
        builder.locationArgument("location");
    }

    @Override
    protected boolean isCorrectArgument(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    @Override
    protected String incorrectArgumentMessage(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    @Override
    protected Vector argumentToValue(List<Object> argument, CommandContext ctx) {
        return ((Location) argument.get(0)).toVector();
    }

    @Override
    protected boolean validateOnSet(String entryName, Vector newValue, CommandContext ctx) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, Vector newValue, CommandContext ctx) {
        return "";
    }

    @Override
    protected String valueToString(Vector vector) {
        return String.format("{x=%.1f,y=%.1f,z=%.1f}", vector.getX(), vector.getY(), vector.getZ());
    }
}
