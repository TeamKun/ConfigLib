package net.kunmc.lab.configlib.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static List<Field> getFieldsIncludingSuperclasses(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getFieldsIncludingSuperclasses(clazz.getSuperclass()));
        }

        return fields;
    }
}
