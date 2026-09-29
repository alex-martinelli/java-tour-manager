package progettoUnibs.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Month;
import java.time.MonthDay;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import progettoUnibs.model.Disponibilita;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.utils.MonthAdapter;
import progettoUnibs.view.OutputPrinter;

public class DisponibilitaFileManager implements DisponibilitaRepository {

    private static final String JSON_FILE_SUFFIX = ".json";
    private static final String DISPONIBILITA_PREFIX = "disponibilita_";
    private static final String ERRORE_LETTURA_FILE = "Errore nella lettura del file: ";
    private static final String ERRORE_SALVATAGGIO_FILE = "Errore durante il salvataggio del file: ";
    
    private OutputPrinter outputPrinter = new OutputPrinter();

    public Disponibilita leggiDisponibilita(Month mese, String organizzazione) {
        String fileName = DISPONIBILITA_PREFIX + mese.toString() + JSON_FILE_SUFFIX;
        String filePath = FileUtils.buildFilePath(organizzazione, fileName);
        File file = new File(filePath);

        if (!file.exists()) {
            return new Disponibilita(false, mese, new HashMap<>());
        }

        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .create();
            Disponibilita d = gson.fromJson(reader, Disponibilita.class);
            if (d.getMese() == null) {
                d.setMese(mese);
            }
            return d;
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
            e.printStackTrace();
            return new Disponibilita(true, mese, new HashMap<>());
        }
    }

    public void salvaDisponibilita(Disponibilita disponibilita, String organizzazione) {
        String fileName = DISPONIBILITA_PREFIX + disponibilita.getMese().toString() + JSON_FILE_SUFFIX;
        String filePath = FileUtils.buildFilePath(organizzazione, fileName);
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .create();
            gson.toJson(disponibilita, writer);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_SALVATAGGIO_FILE + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cancellaFileDisponibilita(String organizzazione, Month meseSuccessivo) {
        new File(FileUtils.buildFilePath(organizzazione, DISPONIBILITA_PREFIX + meseSuccessivo.toString() + JSON_FILE_SUFFIX))
                .delete();
    }
}
