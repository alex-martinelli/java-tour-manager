import static org.junit.Assert.assertTrue;

import java.time.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import progettoUnibs.controller.AttivitaFileManager;
import progettoUnibs.model.*;

public class StatoVisiteTest {

    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final String TITOLO_TEST = "TitoloTest2";
    private static final String DESCRIZIONE_TEST = "Descrizione2";
    private static final String LUOGO_TEST = "qui2";
    private static final int DURATA_MINUTI_TEST = 1;
    private static final int PARTECIPANTI_MIN_TEST = 10;
    private static final int PARTECIPANTI_MAX_TEST = 12;
    private static final int MESE_FINE_TEST = 10;
    private static final int GIORNO_FINE_TEST = 12;
    private static final int ORA_TEST = 0;
    private static final int MINUTI_TEST = 0;
    private static final int SECONDI_TEST = 0;
    private static final String VOLONTARIO_MARIO_TEST = "Mario";
    private static final String FRUITORE_CARLO_TEST = "carlo";
    private static final String VOLONTARIO_MARIO_LOWER_TEST = "mario";
    private static final int CODICE_PRENOTAZIONE_1_TEST = 4567;
    private static final int CODICE_PRENOTAZIONE_2_TEST = 7890;
    private static final int CODICE_PRENOTAZIONE_3_TEST = 0020;
    private static final int NUMERO_ISCRITTI_5_TEST = 5;
    private static final int NUMERO_ISCRITTI_12_TEST = 12;
    private static final int NUMERO_ISCRITTI_0_TEST = 0;
    private static final int GIORNI_FUTURI_1_TEST = 1;
    private static final int GIORNI_FUTURI_5_TEST = 5;

    AttivitaFileManager attivitaFileManager = new AttivitaFileManager();
    Attivita attPrec = attivitaFileManager.leggiAttivitaDaFile(ORGANIZZAZIONE_TEST, LocalDate.now().getMonth());
    ArrayList<Visita> visite = new ArrayList<>();
    TipoVisita tipo = new TipoVisita(TITOLO_TEST, DESCRIZIONE_TEST, LUOGO_TEST, false, DURATA_MINUTI_TEST, PARTECIPANTI_MIN_TEST, PARTECIPANTI_MAX_TEST, MonthDay.now(),
            MonthDay.of(MESE_FINE_TEST, GIORNO_FINE_TEST), List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), LocalTime.of(ORA_TEST, MINUTI_TEST, SECONDI_TEST),
            new ArrayList<>(List.of(VOLONTARIO_MARIO_TEST)));
    ArrayList<Prenotazione> pren = new ArrayList<>();
    Attivita att = new Attivita(LocalDate.now().getMonth(), new ArrayList<>(), visite);

    @Test
    public void testStatoPropostaConfermataVisite() {
        pren.add(new Prenotazione(FRUITORE_CARLO_TEST, CODICE_PRENOTAZIONE_1_TEST, NUMERO_ISCRITTI_5_TEST));
        visite.add(new Visita(tipo, MonthDay.of(LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth() + GIORNI_FUTURI_1_TEST),
                VOLONTARIO_MARIO_LOWER_TEST, NUMERO_ISCRITTI_5_TEST, pren));
        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, att);

        Iterator<Visita> it = att.getVisite().iterator();
        while (it.hasNext()) {
            Visita v = it.next();
            StatoVisita s = v.getStato();

            if (s instanceof StatoProposta) {
                v.getStato().checkStato(v);
                assertTrue(v.getStato() instanceof StatoConfermata);
            }
        }

        // Per visualizzare il file aggiornato con stato CONFERMATA
        // attivitaFileManager.salvaFileAttivita("testFiles", att);

        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, attPrec);
    }

    @Test
    public void testStatoPropostaCompletaVisite() {
        pren.add(new Prenotazione(FRUITORE_CARLO_TEST, CODICE_PRENOTAZIONE_1_TEST, NUMERO_ISCRITTI_12_TEST));
        visite.add(new Visita(tipo, MonthDay.of(LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth() + GIORNI_FUTURI_5_TEST),
                VOLONTARIO_MARIO_LOWER_TEST, NUMERO_ISCRITTI_12_TEST, pren));
        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, att);

        Iterator<Visita> it = att.getVisite().iterator();
        while (it.hasNext()) {
            Visita v = it.next();
            StatoVisita s = v.getStato();

            if (s instanceof StatoProposta) {
                v.getStato().aggiornaStatoNuovaPrenotazione(v);
                assertTrue(v.getStato() instanceof StatoCompleta);
            }
        }

        // Per visualizzare il file aggiornato con stato COMPLETA
        // attivitaFileManager.salvaFileAttivita("testFiles", att);

        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, attPrec);
    }

    @Test
    public void testStatoPropostaCancellataVisite() {
        visite.add(new Visita(tipo, MonthDay.of(LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth() + GIORNI_FUTURI_1_TEST),
                VOLONTARIO_MARIO_LOWER_TEST, NUMERO_ISCRITTI_0_TEST, pren));
        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, att);

        Iterator<Visita> it = att.getVisite().iterator();
        while (it.hasNext()) {
            Visita v = it.next();
            StatoVisita s = v.getStato();

            if (s instanceof StatoProposta) {
                v.getStato().checkStato(v);
                assertTrue(v.getStato() instanceof StatoCancellata);
            }
        }

        // Per visualizzare il file aggiornato con stato CANCELLATA
        // attivitaFileManager.salvaFileAttivita("testFiles", att);

        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, attPrec);
    }

    @Test
    public void testStatoConfermataEffettuataVisite() {
        pren.add(new Prenotazione(FRUITORE_CARLO_TEST, CODICE_PRENOTAZIONE_2_TEST, NUMERO_ISCRITTI_5_TEST));
        visite.add(new Visita(tipo, MonthDay.of(LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth()),
                VOLONTARIO_MARIO_LOWER_TEST, NUMERO_ISCRITTI_5_TEST, pren));
        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, att);

        Iterator<Visita> it = att.getVisite().iterator();
        while (it.hasNext()) {
            Visita v = it.next();
            StatoVisita s = v.getStato();

            if (s instanceof StatoConfermata) {
                v.getStato().checkStato(v);
                assertTrue(v.getStato() instanceof StatoEffettuata);
            }
        }

        // Per visualizzare il file aggiornato con stato EFFETTUATA
        // attivitaFileManager.salvaFileAttivita("testFiles", att);

        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, attPrec);
    }

    @Test
    public void testStatoCompletaEffettuataVisite() {

        pren.add(new Prenotazione(FRUITORE_CARLO_TEST, CODICE_PRENOTAZIONE_3_TEST, NUMERO_ISCRITTI_12_TEST));
        visite.add(new Visita(tipo, MonthDay.of(LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth()),
                VOLONTARIO_MARIO_LOWER_TEST, NUMERO_ISCRITTI_12_TEST, pren));
        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, att);

        Iterator<Visita> it = att.getVisite().iterator();
        while (it.hasNext()) {
            Visita v = it.next();
            StatoVisita s = v.getStato();

            if (s instanceof StatoCompleta) {
                v.getStato().checkStato(v);
                assertTrue(v.getStato() instanceof StatoEffettuata);
            }
        }

        // Per visualizzare il file aggiornato con stato EFFETTUATA
        // attivitaFileManager.salvaFileAttivita("testFiles", att);

        attivitaFileManager.salvaFileAttivita(ORGANIZZAZIONE_TEST, attPrec);
    }
}
