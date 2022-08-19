package net.kunmc.lab.configlib.value.collection;

import org.bukkit.Material;

public class MaterialSetValue extends EnumSetValue<Material> {
    public MaterialSetValue(Material... materials) {
        super(materials);
    }

    public MaterialSetValue(boolean onlyBlock, Material... materials) {
        super(x -> !onlyBlock || x.isBlock());
    }
}