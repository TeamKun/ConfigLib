package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ParticleValue extends SingleValue<Particle> {
    private transient Boolean listable = true;
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
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("ParticleName", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Particle.values())
                    .map(Particle::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Particle.values())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    protected String incorrectArgumentMessage(List<Object> argument) {
        return argument.get(0) + "は不明なParticleです.";
    }

    @Override
    protected Particle argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Particle.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .findFirst()
                .get();
    }

    @Override
    protected boolean validateOnSet(Particle newValue) {
        return true;
    }

    @Override
    protected String invalidValueMessage(String entryName, Particle newValue) {
        return newValue.name() + "は不正な値です.";
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
