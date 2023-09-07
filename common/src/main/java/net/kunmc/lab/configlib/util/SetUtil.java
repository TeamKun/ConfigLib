package net.kunmc.lab.configlib.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {
    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        Set<T> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    public static <T> Set<T> newHashSet(Collection<? extends T> collection) {
        Set<T> set = new HashSet<>(collection.size());
        set.addAll(collection);
        return set;
    }

    private SetUtil() {
    }
}
