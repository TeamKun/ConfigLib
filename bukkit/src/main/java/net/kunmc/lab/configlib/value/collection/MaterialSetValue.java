package net.kunmc.lab.configlib.value.collection;

import org.bukkit.Material;

public class MaterialSetValue extends EnumSetValue<Material> {
    public MaterialSetValue() {
        super(Material.class);
    }

    public MaterialSetValue(boolean onlyBlock) {
        super(Material.class, x -> !onlyBlock || x.isBlock());
    }
}