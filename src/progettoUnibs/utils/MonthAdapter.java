package progettoUnibs.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.MonthDay;

public class MonthAdapter implements JsonSerializer<MonthDay>, JsonDeserializer<MonthDay> {

    @Override
    public JsonElement serialize(MonthDay src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public MonthDay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return MonthDay.parse(json.getAsString());
    }
}
