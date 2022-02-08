package net.kunmc.lab.configlib;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Pair;
import dev.kotx.flylib.command.CommandContext;
import dev.kotx.flylib.command.UsageBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class MapValue<K, V> extends Value<Map<K, V>> implements Map<K, V> {
    private final transient List<Function3<K, V, CommandContext, Boolean>> putListeners = new ArrayList<>();
    private final transient List<Function3<K, V, CommandContext, Boolean>> removeListeners = new ArrayList<>();
    private final transient List<Function3<K, V, CommandContext, Boolean>> clearListeners = new ArrayList<>();

    public MapValue(Map<K, V> value) {
        super(value);
    }

    protected abstract boolean puttableByCommand();

    protected abstract void appendKeyArgumentForPut(UsageBuilder builder);

    protected abstract void appendValueArgumentForPut(UsageBuilder builder);

    protected abstract boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender);

    protected abstract String incorrectKeyArgumentMessageForPut(List<Object> argument);

    protected abstract boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender);

    protected abstract String incorrectValueArgumentMessageForPut(List<Object> argument);

    protected abstract Pair<K, V> argumentToValueForPut(List<Object> argument, CommandSender sender);

    protected abstract boolean validateKeyForPut(K k);

    protected abstract String invalidKeyMessageForPut(String entryName, K k);

    protected abstract boolean validateValueForPut(V v);

    protected abstract String invalidValueMessageForPut(String entryName, V v);

    public <U extends MapValue<K, V>> U onPut(BiConsumer<K, V> listener) {
        return onPut((k, v, ctx) -> {
            listener.accept(k, v);
        });
    }

    public <U extends MapValue<K, V>> U onPut(TriConsumer<K, V, CommandContext> listener) {
        return onPut((k, v, ctx) -> {
            listener.accept(k, v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends MapValue<K, V>> U onPut(Function3<K, V, CommandContext, Boolean> listener) {
        putListeners.add(listener);
        return ((U) this);
    }

    protected boolean onPutValue(K k, V v, CommandContext ctx) {
        return putListeners.stream()
                .map(x -> x.apply(k, v, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForPut(String entryName, K k, V v) {
        return String.format("%sに{%s:%s}を追加しました.", entryName, keyToString(k), valueToString(v));
    }

    protected abstract boolean removableByCommand();

    protected abstract void appendKeyArgumentForRemove(UsageBuilder builder);

    protected abstract void appendValueArgumentForRemove(UsageBuilder builder);

    protected abstract boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender);

    protected abstract String incorrectKeyArgumentMessageForRemove(List<Object> argument);

    protected abstract boolean isCorrectValueArgumentForRemove(List<Object> argument, CommandSender sender);

    protected abstract String incorrectValueArgumentMessageForRemove(List<Object> argument);

    protected abstract Pair<K, V> argumentToValueForRemove(List<Object> argument, CommandSender sender);

    protected abstract boolean validateKeyForRemove(K k);

    protected abstract String invalidKeyMessageForRemove(String entryName, K k);

    protected abstract boolean validateValueForRemove(V v);

    protected abstract String invalidValueMessageForRemove(String entryName, V v);

    public <U extends MapValue<K, V>> U onRemove(BiConsumer<K, V> listener) {
        return onRemove((k, v, ctx) -> {
            listener.accept(k, v);
        });
    }

    public <U extends MapValue<K, V>> U onRemove(TriConsumer<K, V, CommandContext> listener) {
        return onRemove((k, v, ctx) -> {
            listener.accept(k, v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends MapValue<K, V>> U onRemove(Function3<K, V, CommandContext, Boolean> listener) {
        removeListeners.add(listener);
        return ((U) this);
    }

    protected boolean onRemoveValue(K k, V v, CommandContext ctx) {
        return removeListeners.stream()
                .map(x -> x.apply(k, v, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForRemove(String entryName, K k, V v) {
        return String.format("%sから{%s:%s}を削除しました.", entryName, keyToString(k), valueToString(v));
    }

    protected abstract boolean clearableByCommand();

    public <U extends MapValue<K, V>> U onClear(BiConsumer<K, V> listener) {
        return onClear((k, v, ctx) -> {
            listener.accept(k, v);
        });
    }

    public <U extends MapValue<K, V>> U onClear(TriConsumer<K, V, CommandContext> listener) {
        return onClear((k, v, ctx) -> {
            listener.accept(k, v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public <U extends MapValue<K, V>> U onClear(Function3<K, V, CommandContext, Boolean> listener) {
        clearListeners.add(listener);
        return ((U) this);
    }

    protected boolean onClearMap(K k, V v, CommandContext ctx) {
        return clearListeners.stream()
                .map(x -> x.apply(k, v, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    protected abstract String keyToString(K k);

    protected abstract String valueToString(V v);

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        String header = "-----" + entryName + "-----";
        ctx.message(ChatColor.YELLOW + header);

        ctx.success(value.entrySet().stream()
                .map(entry -> String.format("{%s:%s}", keyToString(entry.getKey()), valueToString(entry.getValue())))
                .collect(Collectors.joining(", ")));

        ctx.message(ChatColor.YELLOW + StringUtils.repeat("-", header.length()));
    }

    public int size() {
        return value.size();
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public boolean containsKey(Object key) {
        return value.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    public V get(Object key) {
        return value.get(key);
    }

    public V put(K key, V value) {
        return this.value.put(key, value);
    }

    public V remove(Object value) {
        return this.value.remove(value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        this.value.putAll(m);
    }

    public void clear() {
        value.clear();
    }

    public Set<K> keySet() {
        return value.keySet();
    }

    public Collection<V> values() {
        return value.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return value.entrySet();
    }
}