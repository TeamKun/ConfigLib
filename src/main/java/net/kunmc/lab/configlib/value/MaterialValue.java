package net.kunmc.lab.configlib.value;

import dev.kotx.flylib.command.UsageBuilder;
import dev.kotx.flylib.command.arguments.StringArgument;
import net.kunmc.lab.configlib.command.SingleValue;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class MaterialValue extends SingleValue<Material> {
    private final transient Consumer<Material> consumer;
    private transient Boolean listable = true;
    private transient Boolean writable = true;

    public MaterialValue(Material value) {
        this(value, x -> {
        });
    }

    public MaterialValue(Material value, Consumer<Material> onSet) {
        super(value);
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
    protected void onSetValue(Material newValue) {
        consumer.accept(newValue);
    }

    @Override
    protected boolean validateOnSet(Material newValue) {
        return true;
    }

    @Override
    protected boolean listable() {
        return listable;
    }

    @Override
    protected void appendArgument(UsageBuilder builder) {
        builder.stringArgument("MaterialName", StringArgument.Type.WORD, sb -> {
            Arrays.stream(Material.values())
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .forEach(sb::suggest);
        });
    }

    @Override
    protected boolean isCorrectArgument(List<Object> argument, CommandSender sender) {
        return Arrays.stream(Material.values())
                .anyMatch(m -> m.name().equals(argument.get(0).toString().toUpperCase()));
    }

    @Override
    protected Material argumentToValue(List<Object> argument, CommandSender sender) {
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
    protected boolean writableByCommand() {
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
