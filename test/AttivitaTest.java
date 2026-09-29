import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.util.*;

import org.junit.Test;

import progettoUnibs.controller.*;
import progettoUnibs.model.*;
import progettoUnibs.utils.DateUtils;

public class AttivitaTest {

    private static final String NOME_VISITA_TEST = "Visita Botanica";
    private static final String DESCRIZIONE_VISITA_TEST = "Passeggiata nei giardini";
    private static final String PUNTO_INCONTRO_TEST = "Giardino Est";
    private static final int MINIMO_PARTECIPANTI_TEST = 3;
    private static final int DURATA_MINUTI_TEST = 10;
    private static final int MASSIMO_PARTECIPANTI_TEST = 20;
    private static final int MESE_DICEMBRE = 12;
    private static final int GIORNO_FINE_TEST = 10;
    private static final String VOLONTARIO_MARIO = "mario";
    private static final String VOLONTARIO_BEPPE = "beppe";
    private static final String NOME_LUOGO_TEST = "Roma";
    private static final String ZONA_TEST = "Centro";
    private static final String INDIRIZZO_TEST = "via,roma,2";
    private static final String ORGANIZZAZIONE_TEST = "testFiles";
    private static final String AMBITO_TEST = "via";
    private static final int MAX_PERSONE_ISCRIZIONE_TEST = 5;
    private static final String CARTELLA_MESI_VISITE = "mesiVisite";
    private static final String ESTENSIONE_JSON = ".json";
    private static final int GIORNO_LIMITE_TEST = 15;
    private static final int VALORE_GIORNO_LAVORATIVO_MAX = 6;
    private static final int GIORNO_DISPONIBILITA_TEST = 11;
    private static final int GIORNO_PRECLUSO_TEST = 20;
    private static final String PATTERN_GIORNO = "20";
    private static final int SOGLIA_MESE_DUE_CIFRE = 10;
    private static final String PREFISSO_ZERO = "0";
    private static final String PREFIX_DATA = "--";
    private static final String SEPARATORE_DATA = "-";

    @Test
    public void testAttivita() throws Exception {
        RepositoryManager rm = new RepositoryManager(new AttivitaFileManager(), new CorpoDatiFileManager(), new DisponibilitaFileManager(), null,
                new VolontariFileManager(), null);
        GestioneAttivita ga = new GestioneAttivita(rm);

        TipoVisita visita = new TipoVisita(NOME_VISITA_TEST, DESCRIZIONE_VISITA_TEST, PUNTO_INCONTRO_TEST, false, MINIMO_PARTECIPANTI_TEST, DURATA_MINUTI_TEST,
                MASSIMO_PARTECIPANTI_TEST, MonthDay.now(), MonthDay.of(MESE_DICEMBRE, GIORNO_FINE_TEST), List.of(DayOfWeek.MONDAY), LocalTime.now(),
                new ArrayList<>(List.of(VOLONTARIO_MARIO, VOLONTARIO_BEPPE)));
        Luogo luogo = new Luogo(NOME_LUOGO_TEST, ZONA_TEST, INDIRIZZO_TEST, new ArrayList<>(List.of(visita)));
        CorpoDati corpo = new CorpoDati(new ArrayList<>(List.of(luogo)), ORGANIZZAZIONE_TEST, AMBITO_TEST, MAX_PERSONE_ISCRIZIONE_TEST);
        corpo.getLuoghi().add(luogo);

        Month meseSuccessivo = DateUtils.getMeseSuccessivo();
        File fileAttivita = Path.of("src", ORGANIZZAZIONE_TEST, CARTELLA_MESI_VISITE, meseSuccessivo + ESTENSIONE_JSON).toFile();

        if (fileAttivita.exists()) {
            fileAttivita.delete();
        }

        ga.setDati(corpo);

        assumeTrue(LocalDate.now().getDayOfMonth() >= GIORNO_LIMITE_TEST && LocalDate.now().getDayOfWeek().getValue() < VALORE_GIORNO_LAVORATIVO_MAX);

        Map<String, List<MonthDay>> dateVisite = new HashMap<>();
        dateVisite.put(VOLONTARIO_BEPPE, List.of(MonthDay.of(meseSuccessivo.getValue(), GIORNO_DISPONIBILITA_TEST)));
        Disponibilita disp = new Disponibilita(false, meseSuccessivo, dateVisite);
        DisponibilitaFileManager disponibilitaFileManager = new DisponibilitaFileManager();
        disponibilitaFileManager.salvaDisponibilita(disp, ORGANIZZAZIONE_TEST);
        GestioneVisite gestioneVisite = new GestioneVisite(rm);
        ga.setGestioneVisite(gestioneVisite);
        ga.creaGestioneAttivitaDaCorpoDati(corpo);

        assertTrue(fileAttivita.exists());

        String contenuto = Files.readString(fileAttivita.toPath());
        assertTrue(contenuto.contains(NOME_VISITA_TEST));

        ArrayList<MonthDay> giorni = new ArrayList<>();
        giorni.add(MonthDay.of(meseSuccessivo.getValue(), GIORNO_PRECLUSO_TEST));
        Attivita a = rm.getAttivitaRepository().leggiAttivitaDaFile(ORGANIZZAZIONE_TEST, meseSuccessivo);
        a.setGiorni(giorni);
        rm.getAttivitaRepository().salvaFileAttivita(ORGANIZZAZIONE_TEST, a);

        String contenutoAggiornato = Files.readString(fileAttivita.toPath());
        assertTrue(contenutoAggiornato.contains(PATTERN_GIORNO));
        String meseValue = meseSuccessivo.getValue() >= SOGLIA_MESE_DUE_CIFRE ? String.valueOf(meseSuccessivo.getValue()) : PREFISSO_ZERO + meseSuccessivo.getValue();
        assertTrue(contenutoAggiornato.contains(PREFIX_DATA + meseValue + SEPARATORE_DATA + GIORNO_PRECLUSO_TEST));
    }
}
