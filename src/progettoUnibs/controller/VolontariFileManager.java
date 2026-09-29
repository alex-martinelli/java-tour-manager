package progettoUnibs.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.view.OutputPrinter;

public class VolontariFileManager implements VolontariRepository {

    private static final String VOLONTARI_FILE_SUFFIX = "Volontari.txt";
    private static final String JSON_FILE_SUFFIX = ".json";
    private static final String CORPO_DATI_KEY = "corpoDati";
    private static final String ERRORE_LETTURA_FILE = "Errore nella lettura del file: ";
    private static final String ERRORE_SCRITTURA_FILE = "Errore nella scrittura del file: ";

    private static final String JSON_KEY_LUOGHI = "luoghi";
    private static final String JSON_KEY_TIPI_VISITA = "tipiVisita";
    private static final String JSON_KEY_TITOLO = "titolo";
    private static final String JSON_KEY_VOLONTARI = "volontari";

    private static final String MSG_VOLONTARIO_RIMOSSO = "Volontario rimosso con successo anche dal file.";
    private static final String MSG_ERRORE_MODIFICA_FILE = "Errore durante la modifica del file: ";

    private static final String SEPARATORE_SPAZIO = " ";
    private static final String REGEX_SPAZI = "\\s+";
    private static final String PASSWORD_DEFAULT_SUFFIX = " true";

    private static final int MIN_PASSWORD_RANGE = 1000;
    private static final int MAX_PASSWORD_RANGE = 9000;

    private OutputPrinter outputPrinter = new OutputPrinter();

    public Set<String> leggiVolontariDaTxt(String org) {
        Set<String> volontariTxt = new HashSet<>();
        String txtFilePath = FileUtils.buildFilePath(org, org + VOLONTARI_FILE_SUFFIX);
        try (BufferedReader br = new BufferedReader(new FileReader(txtFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(REGEX_SPAZI);
                if (parts.length > 0) {
                    volontariTxt.add(parts[0]);
                }
            }
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
        }
        return volontariTxt;
    }

    public Set<String> leggiVolontariDaJson(String organizzazione) {
        Set<String> volontariJson = new HashSet<>();
        String jsonFilePath = FileUtils.buildFilePath(organizzazione, organizzazione + JSON_FILE_SUFFIX);
        try (JsonReader reader = new JsonReader(new FileReader(jsonFilePath))) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject corpoDati = jsonObject.getAsJsonObject(CORPO_DATI_KEY);
            if (corpoDati != null) {
                JsonArray luoghi = corpoDati.getAsJsonArray(JSON_KEY_LUOGHI);
                if (luoghi != null) {
                    for (JsonElement luogoElem : luoghi) {
                        JsonObject luogoObj = luogoElem.getAsJsonObject();
                        JsonArray tipiVisita = luogoObj.getAsJsonArray(JSON_KEY_TIPI_VISITA);
                        if (tipiVisita != null) {
                            for (JsonElement tipoElem : tipiVisita) {
                                JsonObject tipoObj = tipoElem.getAsJsonObject();
                                JsonArray volontariArray = tipoObj.getAsJsonArray(JSON_KEY_VOLONTARI);
                                if (volontariArray != null) {
                                    for (JsonElement volElem : volontariArray) {
                                        String vol = volElem.getAsString().trim();
                                        if (!vol.isEmpty()) {
                                            volontariJson.add(vol);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
        }
        return volontariJson;
    }

    public void scriviListaVolontari(String organizzazione, String nomeVolontario) {
        String filePath = FileUtils.buildFilePath(organizzazione, organizzazione + VOLONTARI_FILE_SUFFIX);
        Random random = new Random();
        int number = MIN_PASSWORD_RANGE + random.nextInt(MAX_PASSWORD_RANGE);
        String passwordPredefinita = String.valueOf(number);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("\n" +nomeVolontario + SEPARATORE_SPAZIO + passwordPredefinita + PASSWORD_DEFAULT_SUFFIX);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_SCRITTURA_FILE + e.getMessage());
        }
    }

    public void rimuoviVolontarioDaFile(String nickname, String organizzazione) {
        try {
            String pathString = FileUtils.buildFilePath(organizzazione, organizzazione + VOLONTARI_FILE_SUFFIX);
            Path path = Paths.get(pathString);
            List<String> righeAggiornate = Files.lines(path)
                    .filter(riga -> !riga.contains(nickname) && !riga.trim().isEmpty()) // Elimina la riga con il
                                                                                        // volontario
                    .map(String::trim)
                    .collect(Collectors.toList());

            Files.write(path, righeAggiornate, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            outputPrinter.visualizzaMessaggio(MSG_VOLONTARIO_RIMOSSO);
            List<String> righePulite = Files.lines(path)
                    .map(String::trim)
                    .filter(riga -> !riga.isEmpty())
                    .collect(Collectors.toList());
            Files.write(path, righePulite, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(MSG_ERRORE_MODIFICA_FILE + e.getMessage());
        }
    }

    public Map<String, List<String>> costruisciMappaVolontari(CorpoDati dati) {
        Map<String, List<String>> volontariMap = new HashMap<>();
        String filePath = FileUtils.buildFilePath(dati.getOrganizzazione(), dati.getOrganizzazione() + ".json");
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonObject corpoDati = jsonObject.getAsJsonObject(CORPO_DATI_KEY);
            if (corpoDati != null && corpoDati.has(JSON_KEY_LUOGHI) && corpoDati.get(JSON_KEY_LUOGHI).isJsonArray()) {
                JsonArray luoghi = corpoDati.getAsJsonArray(JSON_KEY_LUOGHI);

                for (JsonElement elemento : luoghi) {
                    JsonObject luogo = elemento.getAsJsonObject();
                    JsonArray tipiVisita = luogo.getAsJsonArray(JSON_KEY_TIPI_VISITA);

                    if (tipiVisita != null) {
                        for (JsonElement visita : tipiVisita) {
                            JsonObject visitaObj = visita.getAsJsonObject();
                            String titoloVisita = visitaObj.get(JSON_KEY_TITOLO).getAsString();
                            JsonArray volontari = visitaObj.getAsJsonArray(JSON_KEY_VOLONTARI);

                            if (volontari != null) {
                                for (JsonElement volontario : volontari) {
                                    String nomeVolontario = volontario.getAsString();
                                    volontariMap.computeIfAbsent(nomeVolontario, k -> new ArrayList<>())
                                            .add(titoloVisita);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
        }
        return volontariMap;
    }

}
