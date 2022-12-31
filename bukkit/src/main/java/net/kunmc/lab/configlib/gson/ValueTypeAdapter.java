package net.kunmc.lab.configlib.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.kunmc.lab.configlib.Value;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Valueクラスのdescriptionとvalueフィールドを先にシリアライズするためのクラス
 */
public class ValueTypeAdapter implements JsonSerializer<Value<?, ?>> {
    @Override
    public JsonElement serialize(Value<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("description", src.description());
        object.add("value", context.serialize(src.value()));

        ReflectionUtils.getFieldsIncludingSuperclasses(src.getClass())
                       .stream()
                       .peek(x -> x.setAccessible(true))
                       .filter(x -> !Modifier.isTransient(x.getModifiers()))
                       .filter(x -> !Modifier.isStatic((x.getModifiers())))
                       .filter(x -> !x.getName()
                                      .equals("description"))
                       .filter(x -> !x.getName()
                                      .equals("value"))
                       .forEach(x -> {
                           try {
                               object.add(x.getName(), context.serialize(x.get(src)));
                           } catch (IllegalAccessException e) {
                               throw new RuntimeException(e);
                           }
                       });
        return object;
    }
}
