package progettoUnibs.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import progettoUnibs.model.Attivita;
import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Disponibilita;
import progettoUnibs.model.Luogo;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Volontario;
import progettoUnibs.utils.DateUtils;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;

public class GestioneDisponibilita {

    private static final String RACCOLTA_APERTA_MESSAGGIO = "Raccolta disponibilità aperta per il mese ";
    private static final String CORPO_DATI_NECESSARIO = "Deve esistere un corpo dati prima di inserire date disponibilità.";
    private static final String NESSUNA_RACCOLTA_APERTA = "Non ci sono mesi con raccolta disponibilità aperta.";
    private static final String GIORNI_DISPONIBILI_PREFIX = "Giorni disponibili per ";
    private static final String NEL_MESE_SUFFIX = " nel mese ";
    private static final String GIORNO_PREFIX = "gg:";
    private static final String INSERISCI_DATA_DISPONIBILITA = "Inserisci data (--MM-DD) disponibilità per il mese ";
    private static final String DATA_NON_APPARTIENE_MESE = "La data inserita non appartiene al mese ";
    private static final String NESSUNA_VISITA_DATA = "Non esistono visite per la data inserita.";
    private static final String DATA_GIA_INSERITA = "Data già inserita.";
    private static final String DATA_INSERITA = "Data inserita.";
    private static final String VUOI_INSERIRE_ALTRA_DATA = "Vuoi inserire un'altra data?";
    private static final int DICEMBRE_MONTH_VALUE = 12;
    private static final int GENNAIO_MONTH_VALUE = 1;

    private InputReader inputReader = new InputReader();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private RepositoryManager repositoryManager;

    public GestioneDisponibilita(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void apriRaccoltaDisponibilita(CorpoDati dati) {
        Month meseDisponibilita = Disponibilita.getMeseDisponibilita();
        Disponibilita disponibilita = repositoryManager.getDisponibilitaRepository().leggiDisponibilita(meseDisponibilita,
                dati.getOrganizzazione());
        disponibilita.setRaccoltaAperta(true);
        repositoryManager.getDisponibilitaRepository().salvaDisponibilita(disponibilita, dati.getOrganizzazione());
        outputPrinter.visualizzaMessaggio(RACCOLTA_APERTA_MESSAGGIO + meseDisponibilita);
    }

    public void aggiungiDisponibilita(Volontario volontario) {
        String organizzazione = volontario.getOrganizzazione();
        CorpoDati dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(organizzazione);
        if (dati == null) {
            outputPrinter.visualizzaMessaggio(CORPO_DATI_NECESSARIO);
            return;
        }
        int currentMonth = LocalDate.now().getMonthValue();

        // Mese +1 (con gestione overflow dicembre -> gennaio)
        Month meseOffset1 = Month.of(currentMonth == DICEMBRE_MONTH_VALUE ? GENNAIO_MONTH_VALUE : currentMonth + 1);
        Disponibilita dispOffset1 = repositoryManager.getDisponibilitaRepository().leggiDisponibilita(meseOffset1, organizzazione);
        boolean dimNulla = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(organizzazione, meseOffset1).getVisite().isEmpty();

        if (!dimNulla) {
            repositoryManager.getDisponibilitaRepository().cancellaFileDisponibilita(organizzazione, meseOffset1);
        }

        // Mese +2 (con gestione overflow dicembre -> gennaio)
        int month2Val = DateUtils.calcolaMeseCorrente(currentMonth);
        Month meseOffset2 = Month.of(month2Val);
        Disponibilita dispOffset2 = repositoryManager.getDisponibilitaRepository().leggiDisponibilita(meseOffset2, organizzazione);

        Disponibilita disponibilita = Disponibilita.getDisponibilitaCorrente(dimNulla, meseOffset1, dispOffset1,
                meseOffset2, dispOffset2);
        if (disponibilita == null) {
            outputPrinter.visualizzaMessaggio(NESSUNA_RACCOLTA_APERTA);
            return;
        }
        Month mese = disponibilita.getMese();
        // Se nessun mese ha raccolta aperta, errore
        if (!(dispOffset1.isRaccoltaAperta()) && !(dispOffset2.isRaccoltaAperta())) {
            outputPrinter.visualizzaMessaggio(NESSUNA_RACCOLTA_APERTA);
            return;
        }

        ArrayList<MonthDay> giorniVisite = new ArrayList<>();
        for (Luogo luogo : dati.getLuoghi()) {
            for (TipoVisita tipo : luogo.getTipiVisita()) {
                if (tipo.getVolontari().contains(volontario.getCredenziali().getUsername())) {
                    Attivita attivita = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(organizzazione, mese);
                    giorniVisite.addAll(attivita.getGiorni(tipo, mese));
                }
            }
            ArrayList<MonthDay> giorniPreclusi = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(organizzazione, mese)
                    .getGiorniPreclusi();
            giorniVisite.removeAll(giorniPreclusi);
        }

        boolean ok = true;
        do {
            outputPrinter.visualizzaMessaggio(
                    GIORNI_DISPONIBILI_PREFIX + volontario.getCredenziali().getUsername() + NEL_MESE_SUFFIX + mese);
            for (MonthDay giorno : giorniVisite) {
                outputPrinter.visualizzaMessaggio(GIORNO_PREFIX + giorno);
            }
            MonthDay data = inputReader.leggiMonthDay(INSERISCI_DATA_DISPONIBILITA + mese);
            if (data.getMonthValue() != mese.getValue()) {
                outputPrinter.visualizzaMessaggio(DATA_NON_APPARTIENE_MESE + mese);
            } else if (!giorniVisite.contains(data)) {
                outputPrinter.visualizzaMessaggio(NESSUNA_VISITA_DATA);
            } else {
                String username = volontario.getCredenziali().getUsername();
                Map<String, List<MonthDay>> map = disponibilita.getDisponibilitaMap();
                if (map.containsKey(username)) {
                    if (map.get(username).contains(data)) {
                        outputPrinter.visualizzaMessaggio(DATA_GIA_INSERITA);
                    } else {
                        map.get(username).add(data);
                        outputPrinter.visualizzaMessaggio(DATA_INSERITA);
                    }
                } else {
                    List<MonthDay> lista = new ArrayList<>();
                    lista.add(data);
                    map.put(username, lista);
                    outputPrinter.visualizzaMessaggio(DATA_INSERITA);
                }
                disponibilita.setDisponibilitaMap(map);
                repositoryManager.getDisponibilitaRepository().salvaDisponibilita(disponibilita, organizzazione);
                ok = InputReader.yesOrNo(VUOI_INSERIRE_ALTRA_DATA);
            }
        } while (ok);
    }

}
