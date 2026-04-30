package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.ConfigCommandDescriptions;

public class MapValueRemoveCommandMessageParameter<K, V> {
    private final String entryName;
    private final CommandContext ctx;
    private final K key;
    private final V value;
    private final ConfigCommandDescriptions.Provider descriptions;

    public MapValueRemoveCommandMessageParameter(String entryName, CommandContext ctx, K key, V value) {
        this(entryName, ctx, key, value, ConfigCommandDescriptions.defaultProvider());
    }

    public MapValueRemoveCommandMessageParameter(String entryName,
                                                 CommandContext ctx,
                                                 K key,
                                                 V value,
                                                 ConfigCommandDescriptions.Provider descriptions) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.key = key;
        this.value = value;
        this.descriptions = descriptions;
    }

    public String entryName() {
        return entryName;
    }

    public CommandContext ctx() {
        return ctx;
    }

    public K key() {
        return key;
    }

    public V value() {
        return value;
    }

    public String describe(ConfigCommandDescriptions.Key key, Object... args) {
        return descriptions.describe(ctx, key, args);
    }
}
