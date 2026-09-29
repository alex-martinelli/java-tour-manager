package progettoUnibs.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.StatoVisita;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.utils.LocalDateAdapter;
import progettoUnibs.utils.MonthAdapter;
import progettoUnibs.utils.StatoVisitaAdapter;

import progettoUnibs.utils.TimeAdapter;
import progettoUnibs.view.OutputPrinter;

public class CorpoDatiFileManager implements CorpoDatiRepository {

    private static final String JSON_FILE_SUFFIX = ".json";
    private static final String CORPO_DATI_KEY = "corpoDati";
    private static final String ERRORE_LETTURA_FILE = "Errore nella lettura del file: ";
    private static final String ERRORE_SALVATAGGIO_FILE = "Errore nel salvataggio dell'organizzazione: ";
    private static final String ERRORE_JSON_NON_CONTIENE_CORPO_DATI = "ERRORE: Il JSON non contiene 'corpoDati'!";
    private static final String IL_CORPO_DATI_NON_ESISTE = "Il corpo dati non esiste ancora!";
    private static final String SRC_PATH = "src";
    private OutputPrinter outputPrinter = new OutputPrinter();

    public CorpoDati leggiCorpoDatiDaFile(String organizzazione) {
        String filePath = FileUtils.buildFilePath(organizzazione, organizzazione + JSON_FILE_SUFFIX);

        if (!Files.exists(Paths.get(filePath))) {
            return null;
        }
        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .registerTypeAdapter(LocalTime.class, new TimeAdapter())
                    .registerTypeAdapter(StatoVisita.class, new StatoVisitaAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject corpoDatiJson = jsonObject.getAsJsonObject(CORPO_DATI_KEY);

            if (corpoDatiJson == null) {
                outputPrinter.visualizzaMessaggio(ERRORE_JSON_NON_CONTIENE_CORPO_DATI);
                return null;
            }

            return gson.fromJson(corpoDatiJson, CorpoDati.class);

        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDataCreated(String org) {
        String filePath = FileUtils.buildFilePath(org, org + JSON_FILE_SUFFIX);
        File file = new File(filePath);
        if (!file.exists()) {
            outputPrinter.visualizzaMessaggio(IL_CORPO_DATI_NON_ESISTE);
            return false;
        }
        return true;
    }

    public void salvaFileCorpoDati(CorpoDati corpo) {
        String organizzazione = corpo.getOrganizzazione();
        String directoryPath = SRC_PATH + File.separator + organizzazione;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = FileUtils.buildFilePath(organizzazione, organizzazione + JSON_FILE_SUFFIX);
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .registerTypeAdapter(LocalTime.class, new TimeAdapter())
                    .registerTypeAdapter(StatoVisita.class, new StatoVisitaAdapter())
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .setPrettyPrinting()
                    .create();

            Map<String, Object> jsonWrapper = new HashMap<>();
            jsonWrapper.put(CORPO_DATI_KEY, corpo);

            String json = gson.toJson(jsonWrapper);
            writer.write(json);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_SALVATAGGIO_FILE + e.getMessage());
            e.printStackTrace();
        }
    }

}
