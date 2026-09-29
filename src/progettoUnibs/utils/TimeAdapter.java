package progettoUnibs.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalTime;

public class TimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalTime.parse(json.getAsString());
    }
}
