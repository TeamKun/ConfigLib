package net.kunmc.lab.configlib.value;

import net.kunmc.lab.configlib.command.Value;
import org.apache.poi.util.NotImplemented;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@NotImplemented
public abstract class MapValue<K, V> extends Value<Map<K, V>> {
    protected Map<K, V> value;

    public MapValue(Map<K, V> value) {
        super(value);
    }

    @Override
    public Map<K, V> value() {
        return value;
    }

    @Override
    public void value(Map<K, V> value) {
        this.value = value;
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
