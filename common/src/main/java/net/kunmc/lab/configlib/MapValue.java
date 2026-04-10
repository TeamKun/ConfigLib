package net.kunmc.lab.configlib;

import net.kunmc.lab.configlib.command.MapValueClearCommandMessageParameter;
import net.kunmc.lab.configlib.command.MapValuePutCommandMessageParameter;
import net.kunmc.lab.configlib.command.MapValueRemoveCommandMessageParameter;
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
    private transient Function<MapValuePutCommandMessageParameter<K, V>, String> successMessageForPut;
    private transient Function<MapValueRemoveCommandMessageParameter<K, V>, String> successMessageForRemove;
    private transient Function<MapValueClearCommandMessageParameter, String> successMessageForClear;

    public MapValue(Map<K, V> value) {
        super(value);
    }

    @Override
    protected Map<K, V> copyValue(Map<K, V> value) {
        return new LinkedHashMap<>(value);
    }

    public final T disablePut() {
        this.puttable = false;
        return ((T) this);
    }

    protected final boolean isPutEnabled() {
        return puttable;
    }

    protected abstract List<PutArgumentDefinition<K, V>> argumentDefinitionsForPut();

    /**
     * Add a listener fired on put command.
     */
    public final T onPut(BiConsumer<K, V> listener) {
        putListeners.add(listener);
        return ((T) this);
    }

    final void dispatchPut(K k, V v) {
        putListeners.forEach(x -> x.accept(k, v));
    }

    /**
     * Sets a custom success message shown after an entry is put via command.
     */
    public final T successMessageForPut(Function<MapValuePutCommandMessageParameter<K, V>, String> successMessage) {
        this.successMessageForPut = successMessage;
        return (T) this;
    }

    protected String succeedMessageForPut(MapValuePutCommandMessageParameter<K, V> param) {
        if (successMessageForPut != null) {
            return successMessageForPut.apply(param);
        }
        return String.format("%sに{%s:%s}を追加しました.",
                             param.entryName(),
                             keyToString(param.key()),
                             valueToString(param.value()));
    }

    public final T disableRemove() {
        this.removable = false;
        return ((T) this);
    }

    protected final boolean isRemoveEnabled() {
        return removable;
    }

    protected abstract List<ArgumentDefinition<K>> argumentDefinitionsForRemove();

    /**
     * Add a listener fired on remove command.
     */
    public final T onRemove(BiConsumer<K, V> listener) {
        removeListeners.add(listener);
        return ((T) this);
    }

    final void dispatchRemove(K k, V v) {
        removeListeners.forEach(x -> x.accept(k, v));
    }

    /**
     * Sets a custom success message shown after an entry is removed via command.
     */
    public final T successMessageForRemove(Function<MapValueRemoveCommandMessageParameter<K, V>, String> successMessage) {
        this.successMessageForRemove = successMessage;
        return (T) this;
    }

    protected String succeedMessageForRemove(MapValueRemoveCommandMessageParameter<K, V> param) {
        if (successMessageForRemove != null) {
            return successMessageForRemove.apply(param);
        }
        return String.format("%sから{%s:%s}を削除しました.",
                             param.entryName(),
                             keyToString(param.key()),
                             valueToString(param.value()));
    }

    public final T disableClear() {
        this.clearable = false;
        return ((T) this);
    }

    protected final boolean isClearEnabled() {
        return clearable;
    }

    /**
     * Add a listener fired on clear command.
     */
    public final T onClear(Runnable listener) {
        clearListeners.add(listener);
        return ((T) this);
    }

    final void dispatchClear() {
        clearListeners.forEach(Runnable::run);
    }

    /**
     * Sets a custom success message shown after the map is cleared via command.
     */
    public final T successMessageForClear(Function<MapValueClearCommandMessageParameter, String> successMessage) {
        this.successMessageForClear = successMessage;
        return (T) this;
    }

    protected String succeedMessageForClear(MapValueClearCommandMessageParameter param) {
        if (successMessageForClear != null) {
            return successMessageForClear.apply(param);
        }
        return param.entryName() + "をクリアしました";
    }

    protected abstract String keyToString(K k);

    protected abstract String valueToString(V v);

    @Override
    protected String defaultDisplayString() {
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

    public static class PutArgumentDefinition<K, V> {
        private final ArgumentDefinition<K> keyDefinition;
        private final ArgumentDefinition<V> valueDefinition;

        public PutArgumentDefinition(ArgumentDefinition<K> k, ArgumentDefinition<V> v) {
            this.keyDefinition = k;
            this.valueDefinition = v;
        }

        public ArgumentDefinition<K> keyDefinition() {
            return keyDefinition;
        }

        public ArgumentDefinition<V> valueDefinition() {
            return valueDefinition;
        }
    }
}
