package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import net.kunmc.lab.configlib.annotation.Internal;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class MaterialValue implements SingleValue<Material> {
    private Material value;
    private final transient Consumer<Material> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        this(value, x -> {
        });
    }

    public MaterialValue(Material value, Consumer<Material> onSet) {
        this.value = value;
        this.consumer = onSet;
    }

    @Override
    public Material value() {
        return value;
    }

    @Override
    public void value(Material value) {
        this.value = value;
    }

    @Override
    @Internal
    public void onSetValue(Material newValue) {
        consumer.accept(newValue);
    }

    @Override
    @Internal
    public boolean validateOnSet(Material newValue) {
        return true;
    }

    @Override
    @Internal
    public boolean listable() {
        return listable;
    }

    @Override
    @Internal
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("MaterialName", sb -> {
            Arrays.stream(Material.values())
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    @Internal
    public boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    @Internal
    public Material argumentToValue(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.get(0).toString().toUpperCase()))
                .findFirst()
                .get();
    }

    public MaterialValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
    @Internal
    public boolean writableByCommand() {
        return writable;
    }

    public MaterialValue writableByCommand(boolean writable) {
        this.writable = writable;
        return this;
    }

    @Override
    public String toString() {
        return String.format("MaterialValue{value=%s,listable=%b,writable=%b}", value().toString(), listable, writable);
    }
}
