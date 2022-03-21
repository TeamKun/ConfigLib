package net.kunmc.lab.configlib.util;

import dev.kotx.flylib.command.Argument;
import dev.kotx.flylib.command.Command;
import dev.kotx.flylib.command.UsageBuilder;

import java.lang.reflect.Field;
import java.util.List;

public class CommandUtil {
    public static String getName(Command command) {
        try {
            Field field = Command.class.getDeclaredField("name");
            field.setAccessible(true);
            return (String) field.get(command);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Command getParent(Command command) {
        try {
            Field field = Command.class.getDeclaredField("parent");
            field.setAccessible(true);
            return ((Command) field.get(command));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Argument<?>> getArguments(UsageBuilder builder) {
        try {
            Field field = UsageBuilder.class.getDeclaredField("arguments");
            field.setAccessible(true);
            return ((List<Argument<?>>) field.get(builder));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addArgument(UsageBuilder builder, Argument<?> argument) {
        getArguments(builder).add(argument);
    }
}
