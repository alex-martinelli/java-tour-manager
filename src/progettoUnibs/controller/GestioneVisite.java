package progettoUnibs.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import progettoUnibs.model.Attivita;
import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.StatoVisita;
import progettoUnibs.model.TipoUtente;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Visita;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.utils.MonthAdapter;
import progettoUnibs.utils.StatoVisitaAdapter;
import progettoUnibs.utils.TimeAdapter;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.OutputVisita;

public class GestioneVisite {

    private static final String CARTELLA_MESI_VISITE = "mesiVisite";
    private static final String ESTENSIONE_JSON = ".json";
    private static final String JSON_KEY_VISITE = "visite";
    private static final String JSON_KEY_STATO = "stato";
    private static final String JSON_KEY_TYPE = "type";
    private static final String JSON_KEY_TIPO_VISITA = "tipo";
    private static final String JSON_KEY_TITOLO = "titolo";
    private static final String JSON_KEY_DATA = "data";
    
    private static final String STATO_PROPOSTA = "PROPOSTA";
    private static final String STATO_CONFERMATA = "CONFERMATA";
    private static final String STATO_CANCELLATA = "CANCELLATA";
    private static final String STATO_COMPLETA = "COMPLETA";
    private static final String STATO_EFFETTUATA = "EFFETTUATA";
    
    private static final String MSG_CARTELLA_NON_ESISTE = "La cartella %s non esiste o non è una cartella.";
    private static final String MSG_ERRORE_LETTURA_FILE = "Errore lettura del file %s: %s";
    private static final String FORMATO_VISITA_CANCELLATA = "------------\ntitolo: %s\ndata: %s\nstato: %s";
    
    private static final int VOLONTARI_INIZIALI = 0;

    private CorpoDati dati;
    private OutputPrinter outputPrinter = new OutputPrinter();
    private OutputVisita visitaView = new OutputVisita();
    private GestioneVolontario gestioneVolontario;
    private RepositoryManager repositoryManager;

    public GestioneVisite(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneVolontario(new GestioneVolontario(repositoryManager));
    }

    public CorpoDati getDati() {
        return dati;
    }

    public void setDati(CorpoDati dati) {
        this.dati = dati;
    }

    public void setGestioneVolontario(GestioneVolontario gestioneVol) {
        gestioneVolontario = gestioneVol;
    }

    public ArrayList<Visita> creaVisite(TipoVisita tipo, ArrayList<MonthDay> elencoGiorni, String organizzazione,
            ArrayList<Visita> visite, Attivita attivita) {
        for (MonthDay giorno : elencoGiorni) {
            if (!gestioneVolontario.esisteVolontarioDisponibile(tipo, organizzazione, giorno)) {
                continue;
            }
            LocalDate date = giorno.atYear(Year.now().getValue());
            if ((visite == null || !attivita.esisteVisitaSovrapposta(tipo, date))) {
                visite.add(new Visita(tipo, giorno,
                        gestioneVolontario.volontarioDisponibile(tipo, organizzazione, giorno), VOLONTARI_INIZIALI, new ArrayList<>()));
            }
        }

        return visite;
    }

    public void visualizzaStatoVisite(TipoUtente tipo) {
        String folderPath = FileUtils.buildFilePath(dati.getOrganizzazione(), CARTELLA_MESI_VISITE);

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            outputPrinter.visualizzaMessaggio(String.format(MSG_CARTELLA_NON_ESISTE, folderPath));
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(ESTENSIONE_JSON));
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray visite = jsonObject.getAsJsonArray(JSON_KEY_VISITE);

                for (JsonElement elemento : visite) {
                    JsonObject visita = elemento.getAsJsonObject();

                    JsonElement statoElement = visita.get(JSON_KEY_STATO);
                    String stato = "";

                    if (statoElement != null) {
                        if (statoElement.isJsonPrimitive()) {
                            stato = statoElement.getAsString();
                        } else if (statoElement.isJsonObject()) {
                            JsonObject statoObj = statoElement.getAsJsonObject();
                            JsonElement typeEl = statoObj.get(JSON_KEY_TYPE);
                            if (typeEl != null && typeEl.isJsonPrimitive()) {
                                stato = typeEl.getAsString();
                            }
                        }
                    }

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(MonthDay.class, new MonthAdapter())
                            .registerTypeAdapter(LocalTime.class, new TimeAdapter())
                            .registerTypeAdapter(StatoVisita.class, new StatoVisitaAdapter())
                            .create();

                    if (STATO_PROPOSTA.equals(stato) || STATO_CONFERMATA.equals(stato)) {
                        Visita visitaObj = gson.fromJson(visita, Visita.class);
                        visitaView.stampaVisita(visitaObj, stato);

                    } else if (STATO_CANCELLATA.equals(stato)) {
                        JsonObject tipoVisita = visita.getAsJsonObject(JSON_KEY_TIPO_VISITA);
                        String titolo = tipoVisita.get(JSON_KEY_TITOLO).getAsString();
                        String data = visita.get(JSON_KEY_DATA).getAsString();

                        outputPrinter.visualizzaMessaggio(String.format(FORMATO_VISITA_CANCELLATA, titolo, data, stato));

                    }

                    if (tipo == TipoUtente.CONFIGURATORE && (STATO_COMPLETA.equals(stato) || STATO_EFFETTUATA.equals(stato))) {
                        Visita visitaObj = gson.fromJson(visita, Visita.class);
                        visitaView.stampaVisita(visitaObj, stato);
                    }
                }

            } catch (IOException e) {
                outputPrinter.visualizzaMessaggio(String.format(MSG_ERRORE_LETTURA_FILE, file.getName(), e.getMessage()));
            }
        }
    }

}
