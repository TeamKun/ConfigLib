package net.kunmc.lab.command;

import dev.kotx.flylib.command.UsageBuilder;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ArgumentType<T> {
    public static final ArgumentType<Integer> INTEGER = new ArgumentType<>(
            b -> b.integerArgument("IntegerArgument"),
            x -> true,
            x -> ((Integer) x),
            int.class
    );
    public static final ArgumentType<Double> DOUBLE = new ArgumentType<>(
            b -> b.doubleArgument("DoubleArgument"),
            x -> true,
            x -> ((Double) x),
            double.class
    );
    public static final ArgumentType<Float> FLOAT = new ArgumentType<>(
            b -> b.floatArgument("FloatArgumnet"),
            x -> true,
            x -> ((Float) x),
            float.class
    );
    public static final ArgumentType<Boolean> BOOLEAN = new ArgumentType<>(
            b -> b.booleanArgument("BooleanArgument",
                    sb -> sb.suggest("true").suggest("false")),
            x -> true,
            x -> ((Boolean) x),
            boolean.class
    );
    public static final ArgumentType<String> STRING = new ArgumentType<>(
            b -> b.textArgument("StringArgument"),
            x -> true,
            Object::toString
    );
    public static final ArgumentType<BlockData> BLOCKDATA = new ArgumentType<>(
            b -> b.textArgument("BlockName", sb -> {
                Arrays.stream(Material.values())
                        .filter(Material::isBlock)
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .forEach(sb::suggest);
            }),
            x -> Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .anyMatch(m -> m.name().equals(x.toString().toUpperCase())),
            x -> Arrays.stream(Material.values())
                    .filter(m -> m.name().equals(x.toString().toUpperCase()))
                    .map(Material::createBlockData)
                    .findFirst()
                    .get()
    );
    public static final ArgumentType<Material> MATERIAL = new ArgumentType<>(
            b -> b.textArgument("MaterialName", sb -> {
                Arrays.stream(Material.values())
                        .map(Material::name)
                        .map(String::toLowerCase)
                        .forEach(sb::suggest);
            }),
            x -> Arrays.stream(Material.values())
                    .anyMatch(m -> m.name().equals(x.toString().toUpperCase())),
            x -> Arrays.stream(Material.values())
                    .filter(m -> m.name().equals(x.toString().toUpperCase()))
                    .findFirst()
                    .get()
    );

    private static final Map<Class, ArgumentType> classArgumentTypeMap = new HashMap<Class, ArgumentType>() {{
        Arrays.stream(ArgumentType.class.getDeclaredFields())
                .peek(x -> x.setAccessible(true))
                .filter(x -> Modifier.isStatic(x.getModifiers()))
                .filter(x -> ArgumentType.class.isAssignableFrom(x.getType()))
                .forEach(x -> {
                    try {
                        ArgumentType type = ((ArgumentType) x.get(null));
                        put(getGenericsClass(x), type);
                        for (Class<?> clazz : type.classes) {
                            put(clazz, type);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }};

    public static <T> ArgumentType<T> byClass(Class<T> clazz) {
        return classArgumentTypeMap.get(clazz);
    }

    public static <T> boolean registerArgumentType(Class<T> clazz, ArgumentType<T> argumentType) {
        return classArgumentTypeMap.putIfAbsent(clazz, argumentType) == null;
    }

    private static Class<?> getGenericsClass(Field field) {
        Class<?> genericsClass = null;
        try {
            String genericsTypeName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
            genericsClass = Class.forName(genericsTypeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return genericsClass;
    }

    private final Consumer<UsageBuilder> appendArgument;
    private final Predicate<Object> isCollectArgument;
    private final Function<Object, T> argumentToValue;
    private final Class<?>[] classes;

    public ArgumentType(Consumer<UsageBuilder> appendArgument, Predicate<Object> isCollectArgument, Function<Object, T> argumentToValue) {
        this(appendArgument, isCollectArgument, argumentToValue, new Class<?>[]{});
    }

    private ArgumentType(Consumer<UsageBuilder> appendArgument, Predicate<Object> isCollectArgument, Function<Object, T> argumentToValue, Class<?>... additional) {
        this.appendArgument = appendArgument;
        this.isCollectArgument = isCollectArgument;
        this.argumentToValue = argumentToValue;
        this.classes = additional;
    }

    public void appendArgument(UsageBuilder builder) {
        appendArgument.accept(builder);
    }

    public boolean isCollectArgument(Object argument) {
        return isCollectArgument.test(argument);
    }

    public T argumentToValue(Object argument) {
        return argumentToValue.apply(argument);
    }
}
