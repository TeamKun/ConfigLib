package net.kunmc.lab.configlib.gson;

import com.google.gson.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class PairTypeAdapter<L, R> implements JsonSerializer<Pair<L, R>>, JsonDeserializer<Pair<L, R>> {
    @Override
    public Pair<L, R> deserialize(JsonElement jsonElement,
                                  Type type,
                                  JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        L left = ctx.deserialize(jsonObject.get("left"), ((ParameterizedType) type).getActualTypeArguments()[0]);
        R right = ctx.deserialize(jsonObject.get("right"), ((ParameterizedType) type).getActualTypeArguments()[1]);

        return MutablePair.of(left, right);
    }

    @Override
    public JsonElement serialize(Pair<L, R> pair, Type type, JsonSerializationContext ctx) {
        JsonObject res = new JsonObject();
        res.add("left", ctx.serialize(pair.getLeft()));
        res.add("right", ctx.serialize(pair.getRight()));

        return res;
    }
}
