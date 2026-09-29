package progettoUnibs.controller;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Luogo;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;

public class GestioneTipoVisita {

    private static final String INSERISCI_TITOLO_TIPO_VISITA = "Inserisci titolo tipo visita\n";
    private static final String INSERISCI_BREVE_DESCRIZIONE = "Inserisci breve descrizione\n";
    private static final String INSERISCI_PUNTO_INCONTRO = "Inserisci punto d'incontro\n";
    private static final String INSERISCI_NUMERO_MINIMO_PARTECIPANTI = "Inserisci numero minimo di partecipanti: ";
    private static final String E_NECESSARIO_COMPRARE_BIGLIETTO = "È necessario comprare un biglietto?";
    private static final String INSERISCI_DURATA_MINUTI = "Inserisci durata (minuti)\n";
    private static final String DURATA_TROPPO_LUNGA = "Durata troppo lunga! Inserisci un valore più realistico.";
    private static final String INSERISCI_NUMERO_MASSIMO_PARTECIPANTI = "Inserisci numero massimo di partecipanti\n";
    private static final String INSERISCI_DATA_INIZIO = "Inserisci data inizio (--MM-DD)";
    private static final String INSERISCI_DATA_FINE = "Inserisci data fine (--MM-DD)";
    private static final String LA_DATA_DI_FINE_DEVE_ESSERE_SUCCESSIVA = "La data di fine deve essere successiva a quella di inizio";
    private static final String PROMPT_NOME_TIPO_VISITA = "Inserisci nome tipo visita: ";
    private static final String MSG_NESSUN_TIPO_VISITA_TROVATO = "Nessun tipo di visita trovato con il nome: ";
    
    private static final int MIN_PARTECIPANTI = 1;
    private static final int MIN_DURATA = 1;
    private static final int MAX_DURATA_MINUTI = 1440;
    private static final int MAX_DURATA_REALISTICA = 10000;

    private OutputPrinter outputPrinter = new OutputPrinter();
    private InputReader inputReader = new InputReader();
    private RepositoryManager repositoryManager;
    private GestioneMenuVolontario gestioneMenuVolontario;

    public GestioneTipoVisita(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneMenuVolontario(new GestioneMenuVolontario(repositoryManager));
    }
    
    public void setGestioneMenuVolontario(GestioneMenuVolontario gestioneMV) {
        gestioneMenuVolontario = gestioneMV;
    }

    public TipoVisita creaTipoVisita(String organizzazione) {
        String titolo = InputReader.leggiStringaNonVuota(INSERISCI_TITOLO_TIPO_VISITA);
        String descrizione = InputReader.leggiStringaNonVuota(INSERISCI_BREVE_DESCRIZIONE);
        String puntoIncontro = InputReader.leggiStringaNonVuota(INSERISCI_PUNTO_INCONTRO);
        int minimoPartecipanti = InputReader.leggiInteroConMinimo(INSERISCI_NUMERO_MINIMO_PARTECIPANTI, MIN_PARTECIPANTI);
        Boolean biglietto = InputReader.yesOrNo(E_NECESSARIO_COMPRARE_BIGLIETTO);
        int durata;

        while (true) {
            durata = InputReader.leggiIntero(INSERISCI_DURATA_MINUTI, MIN_DURATA, MAX_DURATA_MINUTI);
            if (durata > MAX_DURATA_REALISTICA) {
                outputPrinter.visualizzaMessaggio(DURATA_TROPPO_LUNGA);
            } else {
                break;
            }
        }

        int massimoPartecipanti = InputReader.leggiInteroConMinimo(INSERISCI_NUMERO_MASSIMO_PARTECIPANTI,
                minimoPartecipanti);
        MonthDay dataInizio = inputReader.leggiMonthDay(INSERISCI_DATA_INIZIO);
        MonthDay dataFine;
        boolean ok = false;
        do {
            dataFine = inputReader.leggiMonthDay(INSERISCI_DATA_FINE);
            if (dataFine.isBefore(dataInizio)) {
                outputPrinter.visualizzaMessaggio(LA_DATA_DI_FINE_DEVE_ESSERE_SUCCESSIVA);
            } else {
                ok = true;
            }
        } while (!ok);
        List<DayOfWeek> giorniDisponibili = new ArrayList<>();
        inputReader.leggiDayOfWeek(giorniDisponibili);
        LocalTime oraInizio = inputReader.leggiOrario();

        TipoVisita t = new TipoVisita(titolo, descrizione, puntoIncontro, biglietto, minimoPartecipanti, durata,
                massimoPartecipanti, dataInizio, dataFine, giorniDisponibili, oraInizio, null);

        ArrayList<String> volontari = gestioneMenuVolontario.aggiungiVolontariMenu(organizzazione, t);
        t.setVolontari(volontari);

        return t;
    }

    public void aggiungiVolontarioAlTipoVisita(CorpoDati dati) {
        String nome = InputReader.leggiStringaNonVuota(PROMPT_NOME_TIPO_VISITA);
        ArrayList<Luogo> luoghi = dati.getLuoghi();
        boolean trovato = false;
        for (Luogo luogo : luoghi) {
            for (TipoVisita tipoVisita : luogo.getTipiVisita()) {
                if (tipoVisita.getTitolo().equalsIgnoreCase(nome)) {
                    ArrayList<String> volontari = tipoVisita.getVolontari();
                    volontari.addAll(gestioneMenuVolontario.aggiungiVolontariMenu(dati.getOrganizzazione(), tipoVisita));
                    tipoVisita.setVolontari(volontari);
                    trovato = true;
                    luogo.setTipiVisita(luogo.getTipiVisita());
                    dati.setLuoghi(luoghi);
                    repositoryManager.getCorpoDatiRepository().salvaFileCorpoDati(dati);
                }
            }
        }

        if (!trovato) {
            outputPrinter.visualizzaMessaggio(MSG_NESSUN_TIPO_VISITA_TROVATO + nome);
        }
    }

}
