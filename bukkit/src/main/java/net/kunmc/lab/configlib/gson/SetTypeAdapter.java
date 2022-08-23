package net.kunmc.lab.configlib.gson;

import com.google.gson.*;
import net.kunmc.lab.commandlib.Nameable;
import net.kunmc.lab.configlib.util.NameableSet;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

public class SetTypeAdapter implements JsonDeserializer<Set<?>> {
    @Override
    public Set<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jsonArray = json.getAsJsonArray();
        Set<?> set = new LinkedHashSet<>();
        Class<?> clazz = ((Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]);
        for (JsonElement jsonElement : jsonArray) {
            set.add(context.deserialize(jsonElement, clazz));
        }

        if (Nameable.class.isAssignableFrom(clazz)) {
            return new NameableSet<>(((Set<Nameable>) set));
        }
        return set;
    }
}
