package net.kunmc.lab.configlib.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.kunmc.lab.configlib.ConfigKeys;
import net.kunmc.lab.configlib.Value;
import net.kunmc.lab.configlib.util.ConfigUtil;
import net.kunmc.lab.configlib.util.ReflectionUtil;

import java.lang.reflect.Type;

/**
 * Valueクラスのdescriptionとvalueフィールドを先にシリアライズするためのクラス
 */
public class ValueTypeAdapter implements JsonSerializer<Value<?, ?>> {
    @Override
    public JsonElement serialize(Value<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty(ConfigKeys.DESCRIPTION, src.description());
        object.add(ConfigKeys.VALUE, context.serialize(src.value()));

        ReflectionUtil.getFieldsIncludingSuperclasses(src.getClass())
                      .stream()
                      .peek(x -> x.setAccessible(true))
                      .filter(ConfigUtil::isConfigFieldModifier)
                      .filter(x -> !x.getName()
                                     .equals(ConfigKeys.DESCRIPTION))
                      .filter(x -> !x.getName()
                                     .equals(ConfigKeys.VALUE))
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
