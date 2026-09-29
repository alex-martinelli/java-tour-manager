package progettoUnibs.controller;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import progettoUnibs.model.Attivita;
import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.StatoVisita;
import progettoUnibs.model.Visita;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.utils.*;

public class AttivitaFileManager implements AttivitaRepository {

    private static final String JSON_FILE_SUFFIX = ".json";
    private static final String MESI_VISITE_FOLDER = "mesiVisite";
    private static final String VISITE_PROPOSTE_KEY = "visite";
    private static final String DATE_PRECLUSE_KEY = "datePrecluse";
    private static final String ERRORE_LETTURA_FILE = "Errore nella lettura del file: ";
    private static final String ERRORE_SALVATAGGIO_ATTIVITA = "Errore nel salvataggio della gestione attività: ";
    private static final String ERRORE_CREAZIONE_FILE = "Errore nella creazione del file: ";
    private static final String JSON_TEMPLATE_VUOTO = "{\"datePrecluse\":[], \"visite\":[]}";
    private static final String SRC_PATH = "src" + File.separator;

    private OutputPrinter outputPrinter = new OutputPrinter();

    @Override
    public Attivita leggiAttivitaDaFile(String organizzazione, Month mese) {
        String filePath = FileUtils.buildFilePath(organizzazione,
                MESI_VISITE_FOLDER + File.separator + mese.toString() + JSON_FILE_SUFFIX);
        if (!Files.exists(Paths.get(filePath))) {
            File file = new File(filePath);
            try {
                file.createNewFile();
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(JSON_TEMPLATE_VUOTO);
                }
            } catch (IOException e) {
                outputPrinter.visualizzaMessaggio(ERRORE_CREAZIONE_FILE + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        try (JsonReader reader = new JsonReader(new FileReader(filePath))) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .registerTypeAdapter(LocalTime.class, new TimeAdapter())
                    .registerTypeAdapter(StatoVisita.class, new StatoVisitaAdapter())
                    .create();

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray visiteArray = jsonObject.getAsJsonArray(VISITE_PROPOSTE_KEY);
            ArrayList<Visita> visite = new ArrayList<>();
            if (visiteArray != null) {
                for (JsonElement el : visiteArray) {
                    Visita v = gson.fromJson(el, Visita.class);
                    visite.add(v);
                }
            }

            ArrayList<MonthDay> datePrecluse = new ArrayList<>();
            {
                JsonArray datePrecluseArray = jsonObject.getAsJsonArray(DATE_PRECLUSE_KEY);
                for (JsonElement el : datePrecluseArray) {
                    MonthDay md = gson.fromJson(el, MonthDay.class);
                    datePrecluse.add(md);
                }
            }

            return new Attivita(mese, datePrecluse, visite);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_LETTURA_FILE + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void salvaFileAttivita(String organizzazione, Attivita g) {
        String filePath = FileUtils.buildFilePath(organizzazione,
                MESI_VISITE_FOLDER + File.separator + g.getMese().toString() + JSON_FILE_SUFFIX);

        try (FileWriter writer = new FileWriter(filePath)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                    .registerTypeAdapter(LocalTime.class, new TimeAdapter())
                    .registerTypeAdapter(StatoVisita.class, new StatoVisitaAdapter())
                    .setPrettyPrinting()
                    .create();

            ArrayList<Visita> visite = new ArrayList<>();
            for (Visita v : g.getVisite()) {
                visite.add(v);
            }

            Map<String, Object> jsonWrapper = new LinkedHashMap<>();
            jsonWrapper.put(DATE_PRECLUSE_KEY, g.getGiorniPreclusi());
            jsonWrapper.put(VISITE_PROPOSTE_KEY, g.getVisite());
            String json = gson.toJson(jsonWrapper);
            writer.write(json);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(ERRORE_SALVATAGGIO_ATTIVITA + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void checkStatoVisite() {
        Path root = Paths.get(SRC_PATH);

        try (Stream<Path> stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile)
                    .filter(p -> p.getParent().getFileName().toString().equals(MESI_VISITE_FOLDER))
                    .forEach(p -> {
                        String organizzazione = p.getParent().getParent().getFileName().toString();
                        Month mese = Month
                                .valueOf(p.getFileName().toString().replace(JSON_FILE_SUFFIX, "").toUpperCase());

                        Attivita a = leggiAttivitaDaFile(organizzazione, mese);
                        Iterator<Visita> iter = a.getVisite().iterator();
                        while (iter.hasNext()) {
                            Visita v = iter.next();

                            // Controllo dello stato della visita
                            v.getStato().checkStato(v);

                            // Controllo per rimuovere o effettuare
                            v.getStato().rimuoviVisitaCancellata(v, iter);
                        }

                        salvaFileAttivita(organizzazione, a);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void aggiornaVisitaInAttivita(Prenotazione prenotazione, Visita visita, String org) {
        Attivita a = leggiAttivitaDaFile(org, visita.getData().getMonth());
        a.modificaPrenotazioniVisita(visita, prenotazione.getNumeroPersone(), prenotazione, a.getVisite());
        salvaFileAttivita(org, a);
    }

    @Override
    public void rimuoviPrenotazioneDaVisita(int codicePrenotazione, String fruitore, String org, Visita visita) {
        if (visita != null) {
            visita.rimozionePrenotazione(codicePrenotazione, fruitore);
            Attivita a = leggiAttivitaDaFile(org, visita.getData().getMonth());
            for (int i = 0; i < a.getVisite().size(); i++) {
                Visita v = a.getVisite().get(i);
                if (v.getData().equals(visita.getData())
                        && v.getTipoVisita().getTitolo().equals(visita.getTipoVisita().getTitolo())) {
                    a.getVisite().set(i, visita);
                    visita.aggiornaStatoVisita(a);
                    break;
                }
            }

            salvaFileAttivita(org, a);
        }
    }
}
