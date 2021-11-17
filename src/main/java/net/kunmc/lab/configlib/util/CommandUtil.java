package net.kunmc.lab.configlib.util;

import dev.kotx.flylib.command.Command;

import java.lang.reflect.Field;

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
}
