package net.kunmc.lab.configlib.value;

import org.bukkit.Particle;

import java.util.function.Predicate;

public class ParticleValue extends AbstractEnumValue<Particle, ParticleValue> {
    public ParticleValue(Particle value) {
        this(value, x -> true);
    }

    public ParticleValue(Particle value, Predicate<Particle> filter) {
        super(value, filter);
    }
}
