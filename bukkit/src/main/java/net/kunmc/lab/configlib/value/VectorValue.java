package net.kunmc.lab.configlib.value;

import net.kunmc.lab.commandlib.argument.LocationArgument;
import net.kunmc.lab.configlib.ArgumentDefinition;
import net.kunmc.lab.configlib.SingleValue;
import net.kunmc.lab.configlib.util.ListUtil;
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
    protected List<ArgumentDefinition<Vector>> argumentDefinitions() {
        return ListUtil.of(new ArgumentDefinition<>(new LocationArgument("location"), (loc, ctx) -> (loc).toVector()));
    }

    @Override
    protected String valueToString(Vector vector) {
        return String.format("{x=%.1f,y=%.1f,z=%.1f}", vector.getX(), vector.getY(), vector.getZ());
    }
}
