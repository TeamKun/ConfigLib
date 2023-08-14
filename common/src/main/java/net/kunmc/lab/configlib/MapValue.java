package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MapValue<K, V, T extends MapValue<K, V, T>> extends Value<Map<K, V>, T> {
    private final transient List<BiConsumer<K, V>> putListeners = new ArrayList<>();
    private final transient List<BiConsumer<K, V>> removeListeners = new ArrayList<>();
    private final transient List<Runnable> clearListeners = new ArrayList<>();
    private transient boolean puttable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;

    public MapValue(Map<K, V> value) {
        super(value);
    }

    public final T puttableByCommand(boolean puttable) {
        this.puttable = puttable;
        return ((T) this);
    }

    protected final boolean puttableByCommand() {
        return puttable;
    }

    protected abstract void appendKeyArgumentForPut(ArgumentBuilder builder);

    protected abstract void appendValueArgumentForPut(ArgumentBuilder builder);

    protected boolean isCorrectKeyArgumentForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectKeyArgumentMessageForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected boolean isCorrectValueArgumentForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectValueArgumentMessageForPut(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected abstract K argumentToKeyForPut(List<Object> argument, CommandContext ctx);

    protected abstract V argumentToValueForPut(List<Object> argument, CommandContext ctx);

    protected boolean validateKeyForPut(String entryName, K k, CommandContext ctx) {
        return true;
    }

    protected String invalidKeyMessageForPut(String entryName, K k, CommandContext ctx) {
        return "";
    }

    protected boolean validateValueForPut(String entryName, V v, CommandContext ctx) {
        return true;
    }

    protected String invalidValueMessageForPut(String entryName, V v, CommandContext ctx) {
        return "";
    }

    /**
     * Add a listener fired on put command.
     */
    public final T onPut(BiConsumer<K, V> listener) {
        putListeners.add(listener);
        return ((T) this);
    }

    final void onPutValue(K k, V v) {
        putListeners.forEach(x -> x.accept(k, v));
    }

    protected String succeedMessageForPut(String entryName, K k, V v) {
        return String.format("%sに{%s:%s}を追加しました.", entryName, keyToString(k), valueToString(v));
    }

    public final T removableByCommand(boolean removable) {
        this.removable = removable;
        return ((T) this);
    }

    protected final boolean removableByCommand() {
        return removable;
    }

    protected abstract void appendKeyArgumentForRemove(ArgumentBuilder builder);

    protected boolean isCorrectKeyArgumentForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return true;
    }

    protected String incorrectKeyArgumentMessageForRemove(String entryName, List<Object> argument, CommandContext ctx) {
        return "";
    }

    protected abstract K argumentToKeyForRemove(List<Object> argument, CommandContext ctx);

    protected boolean validateKeyForRemove(String entryName, K k, CommandContext ctx) {
        return true;
    }

    protected String invalidKeyMessageForRemove(String entryName, K k, CommandContext ctx) {
        return "";
    }

    /**
     * Add a listener fired on remove command.
     */
    public final T onRemove(BiConsumer<K, V> listener) {
        removeListeners.add(listener);
        return ((T) this);
    }

    final void onRemoveKey(K k, V v) {
        removeListeners.forEach(x -> x.accept(k, v));
    }

    protected String succeedMessageForRemove(String entryName, K k, V v) {
        return String.format("%sから{%s:%s}を削除しました.", entryName, keyToString(k), valueToString(v));
    }

    public final T clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((T) this);
    }

    protected final boolean clearableByCommand() {
        return clearable;
    }

    /**
     * Add a listener fired on clear command.
     */
    public final T onClear(Runnable listener) {
        clearListeners.add(listener);
        return ((T) this);
    }

    final void onClearMap() {
        clearListeners.forEach(Runnable::run);
    }

    protected abstract String keyToString(K k);

    protected abstract String valueToString(V v);

    @Override
    protected String asString() {
        return "{" + value.entrySet()
                          .stream()
                          .map(entry -> String.format("%s:%s",
                                                      keyToString(entry.getKey()),
                                                      valueToString(entry.getValue())))
                          .collect(Collectors.joining(", ")) + "}";
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

    public V getOrDefault(K key, V defaultValue) {
        return value.getOrDefault(key, defaultValue);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        value.forEach(action);
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        value.replaceAll(function);
    }

    @Nullable
    public V putIfAbsent(K key, V value) {
        return this.value.putIfAbsent(key, value);
    }

    public boolean remove(K key, V value) {
        return this.value.remove(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return this.value.replace(key, oldValue, newValue);
    }

    @Nullable
    public V replace(K key, V value) {
        return this.value.replace(key, value);
    }

    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        return value.computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return value.computeIfPresent(key, remappingFunction);
    }

    public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return value.compute(key, remappingFunction);
    }

    public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.value.merge(key, value, remappingFunction);
    }
}
