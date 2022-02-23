package net.kunmc.lab.configlib.value.collection;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;

public class MaterialSetValue extends EnumSetValue<Material> {
    public MaterialSetValue(Material... materials) {
        super(materials);
    }

    public MaterialSetValue(boolean onlyBlock, Material... materials) {
        super(new HashSet<>(),
                Arrays.stream(Material.values())
                        .filter(x -> !onlyBlock || x.isBlock())
                        .toArray(Material[]::new)
                , materials);
    }
}