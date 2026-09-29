package progettoUnibs.controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.Visita;
import progettoUnibs.utils.FileUtils;
import progettoUnibs.view.OutputPrinter;

public class PrenotazioniFileManager implements PrenotazioniRepository {

    private static final String PRENOTAZIONI_FILE = "prenotazioni.json";
    private static final String MSG_CODICE_PRENOTAZIONE = "codice di prenotazione: ";
    private static final String MSG_ERRORE_LETTURA_PRENOTAZIONI = "Errore nella lettura del file prenotazioni: ";
    private static final String MSG_ERRORE_SALVATAGGIO_PRENOTAZIONE = "Errore nel salvataggio della prenotazione: ";
    private static final String MSG_ERRORE_SCRITTURA_PRENOTAZIONI = "Errore nella scrittura del file prenotazioni: ";

    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestionePrenotazioni gestionePrenotazioni;
    private RepositoryManager repositoryManager;

    public PrenotazioniFileManager() {
        setGestionePrenotazioni(new GestionePrenotazioni(repositoryManager));
    }

    public void setGestionePrenotazioni(GestionePrenotazioni gestionePren) {
        gestionePrenotazioni = gestionePren;
    }

    public Prenotazione eseguiIscrizione(Visita visita, String org, String fruitore, int numeroIscrizioni) {
        ArrayList<Prenotazione> prenotazioni = leggiPrenotazioniDaFile(org);
        int codice = Prenotazione.estraiCodice(fruitore, org, prenotazioni);
        outputPrinter.visualizzaMessaggio(MSG_CODICE_PRENOTAZIONE + codice);
        Prenotazione prenotazione = new Prenotazione(fruitore, codice, numeroIscrizioni);
        aggiungiPrenotazioneSuFile(prenotazione, org);

        return prenotazione;
    }

    public void aggiungiPrenotazioneSuFile(Prenotazione nuovaPrenotazione, String org) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        ArrayList<Prenotazione> prenotazioni = new ArrayList<>();
        File file = new File(FileUtils.buildFilePath(org, PRENOTAZIONI_FILE));
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<Prenotazione>>() {
                }.getType();
                prenotazioni = gson.fromJson(reader, listType);
                if (prenotazioni == null) {
                    prenotazioni = new ArrayList<>();
                }
            } catch (IOException e) {
                outputPrinter.visualizzaMessaggio(MSG_ERRORE_LETTURA_PRENOTAZIONI + e.getMessage());
            }
        }
        prenotazioni.add(nuovaPrenotazione);
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(prenotazioni, writer);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(MSG_ERRORE_SALVATAGGIO_PRENOTAZIONE + e.getMessage());
        }
    }

    public ArrayList<Prenotazione> leggiPrenotazioniDaFile(String org) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        ArrayList<Prenotazione> prenotazioni = new ArrayList<>();
        File file = new File(FileUtils.buildFilePath(org, PRENOTAZIONI_FILE));
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<Prenotazione>>() {
                }.getType();
                prenotazioni = gson.fromJson(reader, listType);
                if (prenotazioni == null) {
                    prenotazioni = new ArrayList<>();
                }
            } catch (IOException e) {
                outputPrinter.visualizzaMessaggio(MSG_ERRORE_LETTURA_PRENOTAZIONI + e.getMessage());
            }
        }

        return prenotazioni;
    }

    public void rimuoviPrenotazioneDaFile(int codicePrenotazione, String fruitore, String org) {
        ArrayList<Prenotazione> prenotazioni = leggiPrenotazioniDaFile(org);
        prenotazioni.removeIf(p -> p.getCodice() == codicePrenotazione && p.getNomeFruitore().equals(fruitore));
        Iterator<Prenotazione> iter = prenotazioni.iterator();
        while (iter.hasNext()) {
            Prenotazione p = iter.next();
            if (p.getCodice() == codicePrenotazione && p.getNomeFruitore().equals(fruitore)) {
                iter.remove();
            }
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try (Writer writer = new FileWriter(FileUtils.buildFilePath(org, PRENOTAZIONI_FILE))) {
            gson.toJson(prenotazioni, writer);
        } catch (IOException e) {
            outputPrinter.visualizzaMessaggio(MSG_ERRORE_SCRITTURA_PRENOTAZIONI + e.getMessage());
        }
    }

}
