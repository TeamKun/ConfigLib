package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.function.Consumer;

public final class MaterialValue implements SingleValue<Material> {
    private String materialName;
    private final transient Consumer<Material> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        this(value, x -> {
        });
    }

    public MaterialValue(Material value, Consumer<Material> onSet) {
        this.materialName = value.name();
        this.consumer = onSet;
    }

    @Override
    public Material value() {
        return Material.valueOf(materialName);
    }

    @Override
    public void value(Material value) {
        this.materialName = value.name();
    }

    @Override
    public void onSetValue(Material newValue) {
        consumer.accept(newValue);
    }

    @Override
    public boolean validateOnSet(Material newValue) {
        return true;
    }

    @Override
    public boolean listable() {
        return listable;
    }

    @Override
    public void appendArgument(UsageBuilder builder) {
        builder.textArgument("MaterialName", sb -> {
            Arrays.stream(Material.values())
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    public boolean isCorrectArgument(Object argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .anyMatch(m -> m.name().equals(argument.toString().toUpperCase()));
    }

    @Override
    public Material argumentToValue(Object argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .filter(m -> m.name().equals(argument.toString().toUpperCase()))
                .findFirst()
                .get();
    }

    public MaterialValue listable(boolean listable) {
        this.listable = listable;
        return this;
    }

    @Override
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
