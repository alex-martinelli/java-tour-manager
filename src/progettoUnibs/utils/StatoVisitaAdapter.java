package progettoUnibs.utils;

import com.google.gson.*;
import progettoUnibs.model.*;

import java.lang.reflect.Type;

public class StatoVisitaAdapter implements JsonDeserializer<StatoVisita>, JsonSerializer<StatoVisita> {

    private static final String JSON_KEY_TYPE = "type";
    private static final String STATO_PROPOSTA = "PROPOSTA";
    private static final String STATO_CONFERMATA = "CONFERMATA";
    private static final String STATO_EFFETTUATA = "EFFETTUATA";
    private static final String STATO_CANCELLATA = "CANCELLATA";
    private static final String STATO_COMPLETA = "COMPLETA";
    private static final String STATO_UNKNOWN = "UNKNOWN";
    private static final String MSG_TIPO_SCONOSCIUTO = "Tipo di stato sconosciuto: ";
    private static final String MSG_JSON_NON_VALIDO = "Elemento JSON non valido per StatoVisita: ";
    private static final int OGGETTO_VUOTO_SIZE = 0;

    @Override
    public JsonElement serialize(StatoVisita src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        if (src instanceof StatoProposta) {
            obj.addProperty(JSON_KEY_TYPE, STATO_PROPOSTA);
        } else if (src instanceof StatoConfermata) {
            obj.addProperty(JSON_KEY_TYPE, STATO_CONFERMATA);
        } else if (src instanceof StatoEffettuata) {
            obj.addProperty(JSON_KEY_TYPE, STATO_EFFETTUATA);
        } else if (src instanceof StatoCancellata) {
            obj.addProperty(JSON_KEY_TYPE, STATO_CANCELLATA);
        } else if (src instanceof StatoCompleta) {
            obj.addProperty(JSON_KEY_TYPE, STATO_COMPLETA);
        } else {
            obj.addProperty(JSON_KEY_TYPE, STATO_UNKNOWN);
        }
        return obj;
    }

    @Override
    public StatoVisita deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        // Caso: stringa semplice
        if (json.isJsonPrimitive()) {
            String tipo = json.getAsString();

            switch (tipo) {
                case STATO_PROPOSTA:
                    return new StatoProposta();
                case STATO_CONFERMATA:
                    return new StatoConfermata();
                case STATO_EFFETTUATA:
                    return new StatoEffettuata();
                case STATO_CANCELLATA:
                    return new StatoCancellata();
                case STATO_COMPLETA:
                    return new StatoCompleta();
                default:
                    throw new JsonParseException(MSG_TIPO_SCONOSCIUTO + tipo);
            }
        }

        // Caso: oggetto vuoto
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            if (obj.size() == OGGETTO_VUOTO_SIZE) {
                return new StatoProposta();
            }

            if (obj.has(JSON_KEY_TYPE)) {
                String tipo = obj.get(JSON_KEY_TYPE).getAsString();
                switch (tipo) {
                    case STATO_PROPOSTA:
                        return context.deserialize(json, StatoProposta.class);
                    case STATO_CONFERMATA:
                        return context.deserialize(json, StatoConfermata.class);
                    case STATO_EFFETTUATA:
                        return context.deserialize(json, StatoEffettuata.class);
                    case STATO_CANCELLATA:
                        return context.deserialize(json, StatoCancellata.class);
                    case STATO_COMPLETA:
                        return context.deserialize(json, StatoCompleta.class);
                    default:
                        throw new JsonParseException(MSG_TIPO_SCONOSCIUTO + tipo);
                }
            }
        }

        throw new JsonParseException(MSG_JSON_NON_VALIDO + json);
    }

}
