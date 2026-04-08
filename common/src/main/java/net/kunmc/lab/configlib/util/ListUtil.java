package net.kunmc.lab.configlib.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtil {
    private ListUtil() {
    }

    public static <E> List<E> asList(@Nullable E first, E[] rest) {
        List<E> list = new ArrayList<>();
        list.add(first);
        Collections.addAll(list, rest);

        return list;
    }
}
