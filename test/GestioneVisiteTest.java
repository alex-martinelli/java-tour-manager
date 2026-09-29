import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import progettoUnibs.controller.GestioneVisite;
import progettoUnibs.controller.RepositoryManager;
import progettoUnibs.model.*;

public class GestioneVisiteTest {

    private static final int MESE_GIORNO_TEST = 7;
    private static final int GIORNO_TEST = 14;
    private static final String TITOLO_TEST_1 = "TitoloTest";
    private static final String DESCRIZIONE_TEST_1 = "Descrizione";
    private static final String LUOGO_TEST_1 = "qui";
    private static final String TITOLO_TEST_2 = "TitoloTest2";
    private static final String DESCRIZIONE_TEST_2 = "Descrizione2";
    private static final String LUOGO_TEST_2 = "qui2";
    private static final int DURATA_MINUTI_TEST = 1;
    private static final int PARTECIPANTI_MIN_TEST = 10;
    private static final int PARTECIPANTI_MAX_TEST = 12;
    private static final int MESE_FINE_TEST = 10;
    private static final int GIORNO_FINE_TEST = 12;
    private static final int ORA_TEST = 0;
    private static final int MINUTI_TEST = 0;
    private static final int SECONDI_TEST = 0;
    private static final String VOLONTARIO_TEST = "Mario";
    private static final String ORGANIZZAZIONE_TEST = "TestOrg";
    private static final int VISITE_ATTESE_TEST = 1;
    private static final int VISITE_VUOTE_TEST = 0;

    private GestioneVisite visiteManager;

    @Before
    public void setup() {
        visiteManager = new GestioneVisite(new RepositoryManager(null, null, null, null, null, null)) {
            {
                setGestioneVolontario(new FakeGestioneVolontario());
            }
        };
    }

    @Test
    public void testCreaVisite_CreaUnaVisita() {
        ArrayList<MonthDay> giorni = new ArrayList<>();
        giorni.add(MonthDay.of(MESE_GIORNO_TEST, GIORNO_TEST));

        TipoVisita tipo = new TipoVisita(TITOLO_TEST_1, DESCRIZIONE_TEST_1, LUOGO_TEST_1, false, DURATA_MINUTI_TEST, PARTECIPANTI_MIN_TEST, PARTECIPANTI_MAX_TEST, MonthDay.now(),
                MonthDay.of(MESE_FINE_TEST, GIORNO_FINE_TEST), List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), LocalTime.of(ORA_TEST, MINUTI_TEST, SECONDI_TEST),
                new ArrayList<>(List.of(VOLONTARIO_TEST)));
        ArrayList<Visita> visite = new ArrayList<>();
        Attivita attivita = new Attivita(Month.JULY, giorni, visite);

        ArrayList<Visita> result = visiteManager.creaVisite(tipo, giorni, ORGANIZZAZIONE_TEST, visite, attivita);

        assertEquals(VISITE_ATTESE_TEST, result.size());
        assertEquals(TITOLO_TEST_1, result.get(0).getTipoVisita().getTitolo());
    }

    @Test
    public void testCreaVisite_SaltaGiornoSeNessunVolontario() {
        visiteManager = new GestioneVisite(new RepositoryManager(null, null, null, null, null, null)) {
            {
                setGestioneVolontario(new FakeGestioneVolontario(false));
            }
        };

        ArrayList<MonthDay> giorni = new ArrayList<>();
        giorni.add(MonthDay.of(MESE_GIORNO_TEST, GIORNO_TEST));

        TipoVisita tipo = new TipoVisita(TITOLO_TEST_2, DESCRIZIONE_TEST_2, LUOGO_TEST_2, false, DURATA_MINUTI_TEST, PARTECIPANTI_MIN_TEST, PARTECIPANTI_MAX_TEST, MonthDay.now(),
                MonthDay.of(MESE_FINE_TEST, GIORNO_FINE_TEST), List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), LocalTime.of(ORA_TEST, MINUTI_TEST, SECONDI_TEST),
                new ArrayList<>(List.of(VOLONTARIO_TEST)));
        ArrayList<Visita> visite = new ArrayList<>();
        Attivita attivita = new Attivita(Month.JULY, giorni, visite);
        ArrayList<Visita> result = visiteManager.creaVisite(tipo, giorni, ORGANIZZAZIONE_TEST, visite, attivita);

        assertEquals(VISITE_VUOTE_TEST, result.size());
    }

    static class FakeGestioneVolontario extends progettoUnibs.controller.GestioneVolontario {
        private boolean volontarioPresente;

        public FakeGestioneVolontario() {
            this(true);
        }

        public FakeGestioneVolontario(boolean volontarioPresente) {
            super(null);
            this.volontarioPresente = volontarioPresente;
        }

        @Override
        public boolean esisteVolontarioDisponibile(TipoVisita tipo, String org, MonthDay giorno) {
            return volontarioPresente;
        }

        @Override
        public String volontarioDisponibile(TipoVisita tipo, String org, MonthDay giorno) {
            return VOLONTARIO_TEST;
        }
    }
}
