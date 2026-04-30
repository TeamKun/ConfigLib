package net.kunmc.lab.configlib.schema;

import net.kunmc.lab.configlib.CommonBaseConfig;
import net.kunmc.lab.configlib.ConfigCommandDescriptions;
import net.kunmc.lab.configlib.annotation.ConfigNullable;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Range;
import net.kunmc.lab.configlib.exception.InvalidValueException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PojoConfigSchemaEntry<E> extends ConfigSchemaEntry<E> {
    private final Object root;
    private final Field[] parentChain;
    private final ConfigSchemaValidator<E> validator;
    @Nullable
    private final PojoCommandValue commandValue;

    private PojoConfigSchemaEntry(ConfigSchemaPath path,
                                  String entryName,
                                  Field field,
                                  Object root,
                                  Field[] parentChain,
                                  ConfigSchemaMetadata metadata,
                                  ConfigSchemaValidator<E> validator,
                                  @Nullable PojoCommandValue commandValue) {
        super(path, entryName, field, metadata);
        this.root = Objects.requireNonNull(root, "root");
        this.parentChain = parentChain.clone();
        this.validator = Objects.requireNonNull(validator, "validator");
        this.commandValue = commandValue;
    }

    public static PojoConfigSchemaEntry<Object> from(Object root, Field[] parentChain, Field field) {
        field.setAccessible(true);
        String path = buildPath(parentChain, field);
        return new PojoConfigSchemaEntry<>(new ConfigSchemaPath(path),
                                           path,
                                           field,
                                           root,
                                           parentChain,
                                           new ConfigSchemaMetadata(descriptionOf(field)),
                                           validatorOf(field),
                                           PojoCommandValue.from(parentChain,
                                                                 field,
                                                                 currentValue(root, parentChain, field)));
    }

    private static String buildPath(Field[] parentChain, Field leafField) {
        if (parentChain.length == 0) {
            return leafField.getName();
        }
        StringBuilder sb = new StringBuilder();
        for (Field f : parentChain) {
            sb.append(f.getName())
              .append('.');
        }
        sb.append(leafField.getName());
        return sb.toString();
    }

    private static String descriptionOf(Field field) {
        Description description = field.getAnnotation(Description.class);
        return description == null ? null : description.value();
    }

    private static Object currentValue(Object root, Field[] parentChain, Field leafField) {
        try {
            Object current = root;
            for (Field field : parentChain) {
                current = field.get(current);
                if (current == null) {
                    return null;
                }
            }
            return leafField.get(current);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static ConfigSchemaValidator<Object> validatorOf(Field field) {
        ConfigSchemaValidator<Object> validator = ConfigSchemaValidator.noOp();
        if (!field.isAnnotationPresent(ConfigNullable.class)) {
            validator = validator.and(value -> validateNotNull(field, value));
        }
        Range range = field.getAnnotation(Range.class);
        if (range != null) {
            validator = validator.and(value -> validateRange(field, range, value));
        }
        return validator;
    }

    private static void validateNotNull(Field field, Object value) throws InvalidValueException {
        if (value == null) {
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_NOT_NULL,
                                                                                                      field.getName())),
                                            field.getName() + " must not be null.");
        }
    }

    private static void validateRange(Field field, Range range, Object value) throws InvalidValueException {
        if (value == null) {
            return;
        }
        if (!(value instanceof Number)) {
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_RANGE_NON_NUMERIC,
                                                                                                      field.getName())),
                                            "@Range can only be used on numeric fields: " + field.getName());
        }

        double numericValue = ((Number) value).doubleValue();
        if (numericValue < range.min() || numericValue > range.max()) {
            String min = formatRangeBound(range.min());
            String max = formatRangeBound(range.max());
            throw new InvalidValueException(ctx -> ctx.sendFailure(ConfigCommandDescriptions.describe(ctx,
                                                                                                      ConfigCommandDescriptions.Key.POJO_RANGE,
                                                                                                      field.getName(),
                                                                                                      min,
                                                                                                      max)),
                                            field.getName() + " must be between " + min + " and " + max + ".");
        }
    }

    private static String formatRangeBound(double value) {
        if (value == Math.rint(value) && value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public void validate(E value) throws InvalidValueException {
        validator.validate(value);
    }

    @Override
    public E get() {
        return get((CommonBaseConfig) root);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(CommonBaseConfig config) {
        try {
            Object current = config;
            for (Field f : parentChain) {
                if (current == null) {
                    return null;
                }
                current = f.get(current);
            }
            if (current == null) {
                return null;
            }
            return (E) field().get(current);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(E value) {
        try {
            applySet(value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void applySet(Object leafValue) throws ReflectiveOperationException {
        Object[] holders = new Object[parentChain.length + 1];
        holders[0] = root;
        for (int i = 0; i < parentChain.length; i++) {
            holders[i + 1] = parentChain[i].get(holders[i]);
        }

        Object parent = holders[parentChain.length];
        Object newValue = reconstructLeaf(parent, field(), leafValue);
        if (newValue == null) {
            return; // mutable set already applied
        }

        for (int i = parentChain.length - 1; i >= 0; i--) {
            Object holder = holders[i];
            Field f = parentChain[i];
            Object rebuilt = reconstructParent(holder, f, newValue);
            if (rebuilt == null) {
                return; // mutable set already applied
            }
            newValue = rebuilt;
        }
        throw new IllegalStateException("Reached root without finding a mutable holder");
    }

    /**
     * Returns null if a mutable set was applied directly; otherwise returns the reconstructed object.
     */
    private static Object reconstructLeaf(Object parent,
                                          Field leafField,
                                          Object newValue) throws ReflectiveOperationException {
        if (isRecordClass(parent.getClass())) {
            return reconstructRecord(parent.getClass(), parent, leafField.getName(), newValue);
        }
        if (Modifier.isFinal(leafField.getModifiers())) {
            return reconstructImmutable(parent.getClass(), parent, leafField.getName(), newValue);
        }
        leafField.set(parent, newValue);
        return null;
    }

    /**
     * Returns null if a mutable set was applied directly; otherwise returns the reconstructed object.
     */
    private static Object reconstructParent(Object holder,
                                            Field chainField,
                                            Object newValue) throws ReflectiveOperationException {
        if (isRecordClass(holder.getClass())) {
            return reconstructRecord(holder.getClass(), holder, chainField.getName(), newValue);
        }
        if (Modifier.isFinal(chainField.getModifiers())) {
            return reconstructImmutable(holder.getClass(), holder, chainField.getName(), newValue);
        }
        chainField.set(holder, newValue);
        return null;
    }

    private static Object reconstructImmutable(Class<?> clazz,
                                               Object current,
                                               String changedFieldName,
                                               Object newValue) throws ReflectiveOperationException {
        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
                continue;
            }
            f.setAccessible(true);
            fields.add(f);
        }

        Class<?>[] paramTypes = new Class<?>[fields.size()];
        Object[] args = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            paramTypes[i] = f.getType();
            args[i] = f.getName()
                       .equals(changedFieldName) ? newValue : f.get(current);
        }

        Constructor<?> ctor = clazz.getDeclaredConstructor(paramTypes);
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    // Invoked reflectively to avoid direct Java 16+ API usage in Java 11 source.
    private static boolean isRecordClass(Class<?> type) {
        try {
            return (Boolean) Class.class.getMethod("isRecord")
                                        .invoke(type);
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    private static Object reconstructRecord(Class<?> recordClass,
                                            Object current,
                                            String changedFieldName,
                                            Object newValue) throws ReflectiveOperationException {
        Object[] components = (Object[]) Class.class.getMethod("getRecordComponents")
                                                    .invoke(recordClass);
        int n = components.length;
        if (n == 0) {
            Constructor<?> ctor = recordClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        }

        Class<?> compClass = components[0].getClass();
        Method getName = compClass.getMethod("getName");
        Method getType = compClass.getMethod("getType");
        Method getAccessor = compClass.getMethod("getAccessor");

        Object[] args = new Object[n];
        Class<?>[] paramTypes = new Class<?>[n];
        for (int i = 0; i < n; i++) {
            Object comp = components[i];
            String name = (String) getName.invoke(comp);
            paramTypes[i] = (Class<?>) getType.invoke(comp);
            if (name.equals(changedFieldName)) {
                args[i] = newValue;
            } else {
                Method accessor = (Method) getAccessor.invoke(comp);
                accessor.setAccessible(true);
                args[i] = accessor.invoke(current);
            }
        }

        Constructor<?> ctor = recordClass.getDeclaredConstructor(paramTypes);
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    @Override
    public Object commandObject() {
        return commandValue == null ? get() : commandValue;
    }

    @Override
    public boolean supportsModificationCommand() {
        return commandValue != null;
    }

    @Override
    protected String displayRawString(Object fieldValue) {
        return String.valueOf(fieldValue);
    }

    @Override
    public int sourceHash() {
        return Objects.hashCode(get());
    }

    @Override
    public void dispatchModify() {
    }
}
