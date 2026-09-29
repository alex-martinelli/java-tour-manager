package progettoUnibs.controller;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.StatoProposta;
import progettoUnibs.model.TipoUtente;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.ViewMenu;
import progettoUnibs.view.ViewMenu.MenuOutcome;

public class GestioneMenuFruitore {
    
    private static final String[] VOCI_ACCESSO = { "Accedi", "Registrati" };
    private static final String[] VOCI_FRUITORE = {
            "Effettua iscrizione",
            "Visualizza prenotazioni attive",
            "Disdici iscrizione",
            "Visualizza le visite in stato di visita proposta/confermata/cancellata"
    };
    
    private static final String TITOLO_MENU_ACCESSO = "MENU ACCESSO FRUITORE";
    private static final String TITOLO_MENU_FRUITORE = "MENU FRUITORE";
    
    private static final String PROMPT_ORGANIZZAZIONE = "Inserisci il nome dell'organizzazione: ";
    private static final String MSG_ORGANIZZAZIONE_NON_ESISTENTE = "Organizzazione non esistente, riprova!";
    
    private static final int SCELTA_ACCEDI = 1;
    private static final int SCELTA_REGISTRATI = 2;
    private static final int SCELTA_ISCRIZIONE = 1;
    private static final int SCELTA_VISUALIZZA_PRENOTAZIONI = 2;
    private static final int SCELTA_DISDICI = 3;
    private static final int SCELTA_VISUALIZZA_VISITE = 4;
    private static final int SCELTA_USCITA = 0;

    private ViewMenu viewMenu = new ViewMenu();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneVisite gestioneVisite;
    private GestioneAttivita gestioneAttivita;
    private GestionePrenotazioni gestionePrenotazioni;
    private RepositoryManager repositoryManager;
    private GestioneLogin gestioneLogin;

    public GestioneMenuFruitore(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneVisite(new GestioneVisite(repositoryManager));
        setGestionePrenotazioni(new GestionePrenotazioni(repositoryManager));
        setGestioneAttivita(new GestioneAttivita(repositoryManager));
        setGestioneLogin(new GestioneLogin(repositoryManager, this));
    }

    public void setGestioneAttivita(GestioneAttivita gestioneAtt) {
        gestioneAttivita = gestioneAtt;
    }

    public void setGestionePrenotazioni(GestionePrenotazioni gestionePren) {
        gestionePrenotazioni = gestionePren;
    }

    public void setGestioneLogin(GestioneLogin gestioneLog) {
        gestioneLogin = gestioneLog;
    }

    public void setGestioneVisite(GestioneVisite gestioneVis) {
        gestioneVisite = gestioneVis;
    }

    public void menuAccessoFruitore() {
        int scelta = viewMenu.menuView(TITOLO_MENU_ACCESSO, VOCI_ACCESSO);

        switch (scelta) {
            case SCELTA_REGISTRATI:
                gestioneLogin.registrazioneFruitore();
                break;
            case SCELTA_ACCEDI:
                gestioneLogin.loginFruitore();
                break;
            default:
                viewMenu.showResult(MenuOutcome.USCITA_IN_CORSO);
                break;
        }

    }

    public void menuFruitore(String fruitore) {
        String org = InputReader.leggiStringaNonVuota(PROMPT_ORGANIZZAZIONE);
        if (!repositoryManager.getCredenzialiRepository().checkOrganizzazione(org)) {
            outputPrinter.visualizzaMessaggio(MSG_ORGANIZZAZIONE_NON_ESISTENTE);
            return;
        }
        CorpoDati dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(org);
        if(dati == null) {
            viewMenu.showResult(ViewMenu.MenuOutcome.DATI_NON_PRESENTI);
            return;
        }
        gestioneVisite.setDati(dati);
        int scelta;
        do {

            scelta = viewMenu.menuView(TITOLO_MENU_FRUITORE, VOCI_FRUITORE);
            switch (scelta) {
                case SCELTA_VISUALIZZA_VISITE:
                    gestioneVisite.visualizzaStatoVisite(TipoUtente.FRUITORE);
                    break;
                case SCELTA_DISDICI:
                    gestionePrenotazioni.disdiciPrenotazione(fruitore, org);
                    break;
                case SCELTA_VISUALIZZA_PRENOTAZIONI:
                    gestionePrenotazioni.visualizzaPrenotazioniAttive(fruitore, org);
                    break;
                case SCELTA_ISCRIZIONE:
                    gestioneAttivita.visualizzaVisiteDaAttivita(org, new StatoProposta());
                    gestionePrenotazioni.prenotaVisita(fruitore, org);
                    break;
                case SCELTA_USCITA:
                    viewMenu.showResult(MenuOutcome.USCITA_IN_CORSO);
                    return;
                default:
                    viewMenu.showResult(MenuOutcome.SCELTA_NON_VALIDA);
                    break;
            }
        } while (scelta != SCELTA_USCITA);
    }
}
