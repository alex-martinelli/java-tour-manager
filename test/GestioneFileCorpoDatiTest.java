import static org.junit.Assert.*;

import org.junit.Test;

import progettoUnibs.controller.*;
import progettoUnibs.model.*;
import java.time.*;
import java.util.*;

public class GestioneFileCorpoDatiTest {

    private static final String NOME_VISITA_TEST = "Visita Botanica";
    private static final String DESCRIZIONE_VISITA_TEST = "Passeggiata nei giardini";
    private static final String LUOGO_VISITA_TEST = "Giardino Est";
    private static final int DURATA_MINUTI_TEST = 3;
    private static final int PARTECIPANTI_MIN_TEST = 10;
    private static final int PARTECIPANTI_MAX_TEST = 20;
    private static final int MESE_FINE_TEST = 12;
    private static final int GIORNO_FINE_TEST = 10;
    private static final String VOLONTARIO_1_TEST = "mario";
    private static final String VOLONTARIO_2_TEST = "beppe";
    private static final String NOME_LUOGO_TEST = "Roma";
    private static final String DESCRIZIONE_LUOGO_TEST = "Centro";
    private static final String INDIRIZZO_LUOGO_TEST = "via,roma,2";
    private static final String ORGANIZZAZIONE_TEST = "testFiles";

    @Test
    public void AggiuntaRimozioneLuogoAggiuntaRimuozioneVisita() {
        CorpoDatiRepository corpoDatiRepo = new CorpoDatiFileManager();
        RepositoryManager repo = new RepositoryManager(null, corpoDatiRepo, null, null, null, null);

        TipoVisita visita = new TipoVisita(NOME_VISITA_TEST, DESCRIZIONE_VISITA_TEST, LUOGO_VISITA_TEST, false, DURATA_MINUTI_TEST, PARTECIPANTI_MIN_TEST,
                PARTECIPANTI_MAX_TEST, MonthDay.now(), MonthDay.of(MESE_FINE_TEST, GIORNO_FINE_TEST), List.of(DayOfWeek.MONDAY), LocalTime.now(),
                new ArrayList<>(List.of(VOLONTARIO_1_TEST, VOLONTARIO_2_TEST)));
        Luogo luogo = new Luogo(NOME_LUOGO_TEST, DESCRIZIONE_LUOGO_TEST, INDIRIZZO_LUOGO_TEST, new ArrayList<>(List.of(visita)));
        CorpoDati corpo = corpoDatiRepo.leggiCorpoDatiDaFile(ORGANIZZAZIONE_TEST);
        corpo.getLuoghi().add(luogo);
        corpo.aggiuntaTipoVisitaaLuogo(corpo.getLuoghi(), luogo, visita);
        repo.getCorpoDatiRepository().salvaFileCorpoDati(corpo);

        List<Luogo> luoghi = corpoDatiRepo.leggiCorpoDatiDaFile(ORGANIZZAZIONE_TEST).getLuoghi();
        boolean trovato = luoghi.stream().anyMatch(l -> l.getNomeIdentificativo().equals(NOME_LUOGO_TEST)
                && l.getDescrizione().equals(DESCRIZIONE_LUOGO_TEST) && l.getIndirizzo().equals(INDIRIZZO_LUOGO_TEST));
        assertTrue(trovato);

        RepositoryManager rm = new RepositoryManager(null, new CorpoDatiFileManager(), null, null, null, null);
        GestioneCorpoDati gc = new GestioneCorpoDati(rm);
        CorpoDati c = rm.getCorpoDatiRepository().leggiCorpoDatiDaFile(ORGANIZZAZIONE_TEST);
        gc.setDati(c);
        String tipoVisitaDaRimuovere = NOME_VISITA_TEST;
        boolean rimosso = c.rimuoviTipoVisitaCorpoDati(tipoVisitaDaRimuovere);
        assertTrue(rimosso);
        gc.setDati(c);

        rm.getCorpoDatiRepository().salvaFileCorpoDati(c);

        gc.setDati(c);
        String luogoDaRimuovere = NOME_LUOGO_TEST;
        boolean rimuovi = c.rimuoviLuogoDaCorpoDati(luogoDaRimuovere);
        assertTrue(rimuovi);

        rm.getCorpoDatiRepository().salvaFileCorpoDati(c);
    }
}