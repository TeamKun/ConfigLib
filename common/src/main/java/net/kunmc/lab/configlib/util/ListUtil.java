package net.kunmc.lab.configlib.util;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {
    public static <E> List<E> asList(@Nullable E first, E[] rest) {
        List<E> list = new ArrayList<>();
        list.add(first);
        list.addAll(Arrays.asList(rest));

        return list;
    }

    private ListUtil() {
    }
}
