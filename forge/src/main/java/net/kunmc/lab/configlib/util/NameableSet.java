package net.kunmc.lab.configlib.util;

import net.kunmc.lab.commandlib.Nameable;

import java.util.*;
import java.util.stream.Collectors;

public class NameableSet<E extends Nameable> extends AbstractSet<E> {
    private final transient HashMap<String, E> map = new HashMap<>();

    public NameableSet() {
    }

    public NameableSet(Collection<? extends E> collection) {
        collection.forEach(x -> {
            map.put(x.tabCompleteName(), x);
        });
    }

    @Override
    public Iterator<E> iterator() {
        return map.values()
                  .iterator();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Nameable) {
            return contains(((Nameable) o));
        }
        return false;
    }

    public boolean contains(Nameable n) {
        return map.containsKey(n.tabCompleteName());
    }

    @Override
    public boolean add(E e) {
        return map.put(e.tabCompleteName(), e) == null;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Nameable) {
            return remove(((Nameable) o));
        }
        return false;
    }

    public boolean remove(Nameable n) {
        return map.remove(n.tabCompleteName()) != null;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);

        List<String> list = c.stream()
                             .filter(x -> Nameable.class.isAssignableFrom(x.getClass()))
                             .map(Nameable.class::cast)
                             .map(Nameable::tabCompleteName)
                             .collect(Collectors.toList());
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!list.contains(it.next()
                                 .tabCompleteName())) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }

    public void clear() {
        map.clear();
    }

    public Spliterator<E> spliterator() {
        return map.values()
                  .spliterator();
    }
}
