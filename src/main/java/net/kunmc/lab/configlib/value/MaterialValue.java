package net.kunmc.lab.configlib.value;

import org.bukkit.Material;

import java.util.Arrays;

public class MaterialValue extends EnumValue<Material> {
    public MaterialValue(Material value) {
        super(value);
    }

    public MaterialValue(Material value, boolean onlyBlock) {
        super(value, Arrays.stream(Material.values())
                .filter(x -> !onlyBlock || x.isBlock())
                .toArray(Material[]::new)
        );
    }
}
