package net.kunmc.lab.configlib;

import net.kunmc.lab.commandlib.ArgumentBuilder;
import net.kunmc.lab.commandlib.CommandContext;
import net.kunmc.lab.configlib.function.TriFunction;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MapValue<K, V, T extends MapValue<K, V, T>> extends Value<Map<K, V>, T> {
    private transient boolean puttable = true;
    private transient boolean removable = true;
    private transient boolean clearable = true;
    private final transient List<TriFunction<K, V, CommandContext, Boolean>> putListeners = new ArrayList<>();
    private final transient List<BiFunction<K, CommandContext, Boolean>> removeListeners = new ArrayList<>();
    private final transient List<Function<CommandContext, Boolean>> clearListeners = new ArrayList<>();

    public MapValue(Map<K, V> value) {
        super(value);
    }

    protected boolean puttableByCommand() {
        return puttable;
    }

    public T puttableByCommand(boolean puttable) {
        this.puttable = puttable;
        return ((T) this);
    }

    protected abstract void appendKeyArgumentForPut(ArgumentBuilder builder);

    protected abstract void appendValueArgumentForPut(ArgumentBuilder builder);

    protected abstract boolean isCorrectKeyArgumentForPut(List<Object> argument, CommandSender sender);

    protected abstract String incorrectKeyArgumentMessageForPut(List<Object> argument);

    protected abstract boolean isCorrectValueArgumentForPut(List<Object> argument, CommandSender sender);

    protected abstract String incorrectValueArgumentMessageForPut(List<Object> argument);

    protected abstract K argumentToKeyForPut(List<Object> argument, CommandSender sender);

    protected abstract V argumentToValueForPut(List<Object> argument, CommandSender sender);

    protected boolean validateKeyForPut(K k) {
        return true;
    }

    protected String invalidKeyMessageForPut(String entryName, K k) {
        return "";
    }

    protected boolean validateValueForPut(V v) {
        return true;
    }

    protected String invalidValueMessageForPut(String entryName, V v) {
        return "";
    }

    public T onPut(BiConsumer<K, V> listener) {
        return onPut((k, v, ctx) -> {
            listener.accept(k, v);
        });
    }

    public T onPut(TriConsumer<K, V, CommandContext> listener) {
        return onPut((k, v, ctx) -> {
            listener.accept(k, v, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public T onPut(TriFunction<K, V, CommandContext, Boolean> listener) {
        putListeners.add(listener);
        return ((T) this);
    }

    protected boolean onPutValue(K k, V v, CommandContext ctx) {
        return putListeners.stream()
                .map(x -> x.apply(k, v, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForPut(String entryName, K k, V v) {
        return String.format("%sに{%s:%s}を追加しました.", entryName, keyToString(k), valueToString(v));
    }

    protected boolean removableByCommand() {
        return removable;
    }

    public T removableByCommand(boolean removable) {
        this.removable = removable;
        return ((T) this);
    }

    protected abstract void appendKeyArgumentForRemove(ArgumentBuilder builder);

    protected abstract boolean isCorrectKeyArgumentForRemove(List<Object> argument, CommandSender sender);

    protected abstract String incorrectKeyArgumentMessageForRemove(List<Object> argument);

    protected abstract K argumentToKeyForRemove(List<Object> argument, CommandSender sender);

    protected boolean validateKeyForRemove(K k) {
        return value.containsKey(k);
    }

    protected String invalidKeyMessageForRemove(String entryName, K k) {
        return String.format("%sは%sに追加されていませんでした.", keyToString(k), entryName);
    }

    public T onRemove(Consumer<K> listener) {
        return onRemove((k, ctx) -> {
            listener.accept(k);
        });
    }

    public T onRemove(BiConsumer<K, CommandContext> listener) {
        return onRemove((k, ctx) -> {
            listener.accept(k, ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public T onRemove(BiFunction<K, CommandContext, Boolean> listener) {
        removeListeners.add(listener);
        return ((T) this);
    }

    protected boolean onRemoveKey(K k, CommandContext ctx) {
        return removeListeners.stream()
                .map(x -> x.apply(k, ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String succeedMessageForRemove(String entryName, K k, V v) {
        return String.format("%sから{%s:%s}を削除しました.", entryName, keyToString(k), valueToString(v));
    }

    protected boolean clearableByCommand() {
        return clearable;
    }

    public T clearableByCommand(boolean clearable) {
        this.clearable = clearable;
        return ((T) this);
    }

    public T onClear(Runnable listener) {
        return onClear(ctx -> {
            listener.run();
        });
    }

    public T onClear(Consumer<CommandContext> listener) {
        return onClear(ctx -> {
            listener.accept(ctx);
            return false;
        });
    }

    /**
     * @return true if you want to cancel event, otherwise false
     */
    public T onClear(Function<CommandContext, Boolean> listener) {
        clearListeners.add(listener);
        return ((T) this);
    }

    protected boolean onClearMap(CommandContext ctx) {
        return clearListeners.stream()
                .map(x -> x.apply(ctx))
                .reduce(false, (a, b) -> a || b);
    }

    protected String clearMessage(String entryName) {
        return entryName + "をクリアしました.";
    }

    protected abstract String keyToString(K k);

    protected abstract String valueToString(V v);

    @Override
    protected void sendListMessage(CommandContext ctx, String entryName) {
        ctx.sendSuccess(entryName + ": {" + value.entrySet().stream()
                .map(entry -> String.format("%s:%s", keyToString(entry.getKey()), valueToString(entry.getValue())))
                .collect(Collectors.joining(", ")) + "}");
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
