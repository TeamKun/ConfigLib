package net.kunmc.lab.configlib.value;

import org.bukkit.Material;

public class MaterialValue extends AbstractEnumValue<Material, MaterialValue> {
    public MaterialValue(Material value) {
        this(value, false);
    }

    public MaterialValue(Material value, boolean onlyBlock) {
        super(value,
                value.getDeclaringClass().getEnumConstants(),
                x -> !onlyBlock || x.isBlock());
    }
}
