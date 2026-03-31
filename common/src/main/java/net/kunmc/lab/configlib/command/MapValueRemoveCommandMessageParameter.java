package net.kunmc.lab.configlib.command;

import net.kunmc.lab.commandlib.CommandContext;

public class MapValueRemoveCommandMessageParameter<K, V> {
    private final String entryName;
    private final CommandContext ctx;
    private final K key;
    private final V value;

    public MapValueRemoveCommandMessageParameter(String entryName, CommandContext ctx, K key, V value) {
        this.entryName = entryName;
        this.ctx = ctx;
        this.key = key;
        this.value = value;
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
}
