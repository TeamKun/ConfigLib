package net.kunmc.lab.configlib.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {
    public static List<Field> getFieldsIncludingSuperclasses(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getFieldsIncludingSuperclasses(clazz.getSuperclass()));
        }

        return fields;
    }

    private ReflectionUtil() {
    }
}
