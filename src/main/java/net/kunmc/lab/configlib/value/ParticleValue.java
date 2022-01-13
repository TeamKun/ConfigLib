package net.kunmc.lab.configlib.value;

import org.bukkit.Particle;

public class ParticleValue extends EnumValue<Particle> {
    private transient boolean listable = true;
    private transient boolean writable = true;

    public ParticleValue(Particle value) {
        super(value);
    }

    @Override
    protected boolean writableByCommand() {
        return writable;
    }

    public ParticleValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    public ParticleValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }
}
