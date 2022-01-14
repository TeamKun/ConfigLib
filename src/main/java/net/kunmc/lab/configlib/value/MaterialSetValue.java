package net.kunmc.lab.configlib.value;

import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MaterialSetValue extends EnumSetValue<Material> {
    public MaterialSetValue(Material... materials) {
        this(Sets.newHashSet(materials));
    }

    public MaterialSetValue(@NotNull Set<Material> value) {
        super(value);
    }
}