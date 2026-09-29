package progettoUnibs.controller;

import java.time.LocalDate;

import java.time.Month;
import java.time.MonthDay;
import progettoUnibs.utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import progettoUnibs.model.*;
import progettoUnibs.view.*;

public class GestioneAttivita {

    private static final String IL_MESE_CORRENTE_E_GIA_STATO_CREATO = "Il mese corrente è già stato creato";
    private static final String CREAZIONE_VISITE_NON_PERMESSA = "La creazione delle visite è possibile solo successivamente al 15 (giorno feriale)";
    private static final String INSERISCI_DATA_PRECLUSA = " - Inserisci data preclusa (--MM-DD): ";
    private static final String DATA_NON_VALIDA = "Data non valida, inserire una data del mese ";
    private static final String VUOI_INSERIRE_ALTRA_DATA = "Vuoi inserire un'altra data preclusa?";
    private static final String DATA_GIA_PRECLUSA = "Data già preclusa, inserire un'altra data.";
    private static final String ATTIVITA_CREATA_MESSAGGIO = "Attività per %s create. Raccolta disponibilità volontari chiusa.";
    private static final int GIORNO_LIMITE_CREAZIONE = 15;
    private static final int GIORNO_SETTIMANA_LIMITE = 6;
    private static final int GIORNO_LIMITE_DATE_PRECLUSE = 16;
    private static final int MESI_ANTICIPO_NORMALE = 2;
    private static final int MESI_ANTICIPO_TARDIVO = 3;
    private static final int MESI_IN_ANNO = 12;

    private Attivita attivita;
    private CorpoDati dati;
    private InputReader inputReader = new InputReader();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private OutputVisita outputVisita = new OutputVisita();
    private RepositoryManager repositoryManager;
    private GestioneVisite gestioneVisite;

    public GestioneAttivita(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public Attivita getAttivita() {
        return attivita;
    }

    public void setAttivita(Attivita attivita) {
        this.attivita = attivita;
        attivita = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(dati.getOrganizzazione(),
                attivita.getMese());
    }

    public void setGestioneVisite(GestioneVisite gestioneVisite) {
        this.gestioneVisite = gestioneVisite;
    }

    public CorpoDati getDati() {
        return dati;
    }

    public void setDati(CorpoDati dati) {
        this.dati = dati;
        dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(dati.getOrganizzazione());
    }

    public void creaGestioneAttivitaDaCorpoDati(CorpoDati corpo) {
        Month meseSuccessivo = DateUtils.getMeseSuccessivo();
        setGestioneVisite(new GestioneVisite(repositoryManager));
        if (repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(corpo.getOrganizzazione(), meseSuccessivo)
                .getVisite()
                .size() != 0) {
            outputPrinter.visualizzaMessaggio(IL_MESE_CORRENTE_E_GIA_STATO_CREATO);
            return;
        }

        if (LocalDate.now().getDayOfMonth() < GIORNO_LIMITE_CREAZIONE && LocalDate.now().getDayOfWeek().getValue() < GIORNO_SETTIMANA_LIMITE) {
            outputPrinter.visualizzaMessaggio(CREAZIONE_VISITE_NON_PERMESSA);
            return;
        }

        ArrayList<Luogo> luoghi = corpo.getLuoghi();
        Map<Month, Attivita> gestionePerMese = new HashMap<>();
        Attivita ga = null;
        for (Luogo luogo : luoghi) {
            ArrayList<TipoVisita> tipiVisita = luogo.getTipiVisita();
            for (TipoVisita t : tipiVisita) {
                t.setLuogo(luogo);

                ga = gestionePerMese.computeIfAbsent(meseSuccessivo, m -> {
                    Attivita esistente = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(
                            corpo.getOrganizzazione(),
                            meseSuccessivo);
                    return (esistente != null) ? esistente
                            : new Attivita(m, new ArrayList<>(), new ArrayList<>());
                });
                ArrayList<MonthDay> giorni = ga.getGiorni(t, meseSuccessivo);
                ArrayList<Visita> visite = gestioneVisite.creaVisite(t, giorni, corpo.getOrganizzazione(),
                        ga.getVisite(), ga);
                ga.setVisite(visite);
            }
        }

        repositoryManager.getAttivitaRepository().salvaFileAttivita(corpo.getOrganizzazione(), ga);
        repositoryManager.getDisponibilitaRepository().cancellaFileDisponibilita(corpo.getOrganizzazione(),
                meseSuccessivo);

        outputPrinter.visualizzaMessaggio(String.format(ATTIVITA_CREATA_MESSAGGIO, meseSuccessivo));
    }

    public void inserireDatePrecluse() {
        String organizzazione = getDati().getOrganizzazione();
        Boolean ok = true;
        ArrayList<MonthDay> datePrecluse = new ArrayList<>();
        ArrayList<MonthDay> datePreclusePresenti;
        Month mese=LocalDate.now().getDayOfMonth() >= GIORNO_LIMITE_DATE_PRECLUSE ? LocalDate.now().plusMonths(MESI_ANTICIPO_TARDIVO).getMonth()
                : LocalDate.now().plusMonths(MESI_ANTICIPO_NORMALE).getMonth();
            datePreclusePresenti = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(organizzazione, mese)
                    .getGiorniPreclusi();
            while (ok) {
                MonthDay data = inputReader.leggiMonthDay(INSERISCI_DATA_PRECLUSA);
                if (data.getMonthValue() != mese.getValue()) {
                    outputPrinter.visualizzaMessaggio(DATA_NON_VALIDA + mese);
                    continue;
                } else if (datePrecluse.contains(data) || datePreclusePresenti.contains(data)) {
                    outputPrinter.visualizzaMessaggio(DATA_GIA_PRECLUSA);
                    continue;
                }
                datePrecluse.add(data);
                ok = InputReader.yesOrNo(VUOI_INSERIRE_ALTRA_DATA);
            }

        attivita = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(organizzazione, mese);
        ArrayList<MonthDay> datePrecluseGestione = attivita.getGiorniPreclusi();
        datePrecluseGestione.addAll(datePrecluse);
        attivita.setGiorni(datePrecluseGestione);
        repositoryManager.getAttivitaRepository().salvaFileAttivita(organizzazione, attivita);
    }

    public ArrayList<Visita> caricaVisite(String org, StatoVisita stato) {
        ArrayList<Visita> visiteProposte = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            Month mese = Month.of(LocalDate.now().getMonthValue() + i);
            if (mese.getValue() > MESI_IN_ANNO) {
                mese = Month.of(mese.getValue() - MESI_IN_ANNO);
            }
            Attivita attivita = repositoryManager.getAttivitaRepository().leggiAttivitaDaFile(org, mese);
            if (attivita != null) {
                for (Visita visita : attivita.getVisite()) {
                    if (visita.getStato().getNome().equals(stato.getNome())) {
                        visiteProposte.add(visita);
                    }
                }
            }
        }
        return visiteProposte;
    }

    public void visualizzaVisiteDaAttivita(String org, StatoVisita stato) {
        ArrayList<Visita> visite = caricaVisite(org, stato);
        outputVisita.visualizzaVisite(visite);
    }

}
