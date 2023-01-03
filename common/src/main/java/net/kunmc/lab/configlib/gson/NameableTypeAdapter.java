package net.kunmc.lab.configlib.gson;

import com.google.gson.*;
import net.kunmc.lab.commandlib.Nameable;
import org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class NameableTypeAdapter implements JsonSerializer<Nameable>, JsonDeserializer<Nameable> {
    @Override
    public JsonElement serialize(Nameable src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("class",
                           src.getClass()
                              .getName());

        ReflectionUtils.getFieldsIncludingSuperclasses(src.getClass())
                       .stream()
                       .peek(x -> x.setAccessible(true))
                       .filter(x -> !Modifier.isTransient(x.getModifiers()))
                       .filter(x -> !Modifier.isStatic((x.getModifiers())))
                       .forEach(x -> {
                           try {
                               object.add(x.getName(), context.serialize(x.get(src)));
                           } catch (IllegalAccessException e) {
                               throw new RuntimeException(e);
                           }
                       });

        return object;
    }

    @Override
    public Nameable deserialize(JsonElement json,
                                Type typeOfT,
                                JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String className = object.get("class")
                                 .getAsString();
        try {
            Class<?> clazz = Class.forName(className);
            Object result = clazz.getConstructor()
                                 .newInstance();

            for (Field f : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                if (Modifier.isTransient(f.getModifiers())) {
                    continue;
                }

                JsonElement e = object.get(f.getName());
                if (e == null) {
                    continue;
                }
                f.set(result, context.deserialize(e, f.getType()));
            }

            return ((Nameable) result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
