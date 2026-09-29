import static org.junit.Assert.assertEquals;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import progettoUnibs.controller.*;
import progettoUnibs.model.*;

public class GestionePrenotazioniTest {

    private static final int CODICE_PRENOTAZIONE_TEST = 4567;
    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final String NOME_TIPO_VISITA_TEST = "citta alta";
    private static final String DESCRIZIONE_TIPO_VISITA_TEST = "visita a città alta";
    private static final String LUOGO_TIPO_VISITA_TEST = "piazza vecchia";
    private static final int DURATA_MINUTI_TEST = 15;
    private static final int PARTECIPANTI_MIN_TEST = 120;
    private static final int PARTECIPANTI_MAX_TEST = 50;
    private static final int MESE_INIZIO_TEST = 1;
    private static final int GIORNO_INIZIO_TEST = 1;
    private static final int MESE_FINE_TEST = 12;
    private static final int GIORNO_FINE_TEST = 31;
    private static final int ORA_VISITA_TEST = 10;
    private static final int MINUTI_VISITA_TEST = 0;
    private static final int SECONDI_VISITA_TEST = 0;
    private static final String VOLONTARIO_1_TEST = "mattia";
    private static final String VOLONTARIO_2_TEST = "alex";
    private static final int MESE_VISITA_TEST = 7;
    private static final int GIORNO_VISITA_TEST = 29;
    private static final int POSIZIONE_CODA_TEST = 0;
    private static final int NUMERO_ISCRITTI_TEST = 6;
    private static final String FRUITORE_TEST = "carlo";

    int codicePrenotazione=CODICE_PRENOTAZIONE_TEST;
    String org = ORGANIZZAZIONE_TEST;

    @Test
    public void testPrenotazione() {
        RepositoryManager rm = new RepositoryManager(new AttivitaFileManager(), new CorpoDatiFileManager(), null, null,
                null,
                new PrenotazioniFileManager());
        GestionePrenotazioni gestionePrenotazioni = new GestionePrenotazioni(rm);
        TipoVisita tipo = new TipoVisita(NOME_TIPO_VISITA_TEST, DESCRIZIONE_TIPO_VISITA_TEST, LUOGO_TIPO_VISITA_TEST, true, DURATA_MINUTI_TEST, PARTECIPANTI_MIN_TEST, PARTECIPANTI_MAX_TEST,
                MonthDay.of(MESE_INIZIO_TEST, GIORNO_INIZIO_TEST),
                MonthDay.of(MESE_FINE_TEST, GIORNO_FINE_TEST), List.of(DayOfWeek.THURSDAY), LocalTime.of(ORA_VISITA_TEST, MINUTI_VISITA_TEST, SECONDI_VISITA_TEST),
                new ArrayList<>(List.of(VOLONTARIO_1_TEST, VOLONTARIO_2_TEST)));
        Visita visita = new Visita(tipo, MonthDay.of(MESE_VISITA_TEST, GIORNO_VISITA_TEST), VOLONTARIO_1_TEST, POSIZIONE_CODA_TEST, new ArrayList<>());
        int numeroIscritti = NUMERO_ISCRITTI_TEST;
        String fruitore = FRUITORE_TEST;
        Prenotazione prenotazione = new Prenotazione(fruitore, codicePrenotazione, numeroIscritti);
        rm.getPrenotazioniRepository().aggiungiPrenotazioneSuFile(prenotazione, org);
        prenotazione.setCodice(codicePrenotazione);
        rm.getAttivitaRepository().aggiornaVisitaInAttivita(prenotazione, visita, org);
        Visita v = gestionePrenotazioni.trovaVisitaDaCodicePrenotazione(codicePrenotazione, org);

        assertEquals(visita.getData(), v.getData());
    }

    @Test
    public void testDisdiciPrenotazione() {
        RepositoryManager rm = new RepositoryManager(new AttivitaFileManager(), new CorpoDatiFileManager(), null, null,
                null,
                new PrenotazioniFileManager());
        GestionePrenotazioni gestionePrenotazioni = new GestionePrenotazioni(rm);
        Visita visita = gestionePrenotazioni.trovaVisitaDaCodicePrenotazione(codicePrenotazione, ORGANIZZAZIONE_TEST);
        rm.getAttivitaRepository().rimuoviPrenotazioneDaVisita(codicePrenotazione, FRUITORE_TEST, org, visita);
        rm.getPrenotazioniRepository().rimuoviPrenotazioneDaFile(codicePrenotazione, FRUITORE_TEST, org);   
        
        assertEquals(null, gestionePrenotazioni.trovaVisitaDaCodicePrenotazione(codicePrenotazione, org));
    }
}
