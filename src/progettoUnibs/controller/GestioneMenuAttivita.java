package progettoUnibs.controller;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.ViewMenu;
import progettoUnibs.view.ViewMenu.MenuOutcome;

public class GestioneMenuAttivita {

    private static final String[] VOCI_AGGIUNTA_DATI = {
            "Aggiungi un volontario",
            "Aggiungi un luogo",
            "Aggiungi un tipo di visita a un luogo"
    };
    
    private static final String[] VOCI_RIMOZIONE_DATI = {
            "Rimuovi un volontario",
            "Rimuovi un luogo",
            "Rimuovi un tipo di visita da un luogo"
    };
    
    private static final String TITOLO_MENU_AGGIUNTA = "MENU AGGIUNTA DATI ATTIVITA";
    private static final String TITOLO_MENU_RIMOZIONE = "MENU RIMOZIONE DATI ATTIVITA";
    private static final String MSG_USCITA_MENU_RIMOZIONE = "Uscita dal menu di aggiunta dati attività...";
    private static final String MSG_SCELTA_NON_VALIDA = "Scelta non valida, riprova.";
    
    private static final int SCELTA_VOLONTARIO = 1;
    private static final int SCELTA_LUOGO = 2;
    private static final int SCELTA_TIPO_VISITA = 3;
    private static final int SCELTA_USCITA = 0;

    private ViewMenu viewMenu = new ViewMenu();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneCorpoDati gestioneCorpoDati;
    private GestioneLuogo gestioneLuogo;
    private GestioneTipoVisita gestioneTipoVisita;
    private RepositoryManager repositoryManager;

    public GestioneMenuAttivita(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
        setGestioneLuogo(new GestioneLuogo(repositoryManager));
        setGestioneTipoVisita(new GestioneTipoVisita(repositoryManager));
    }

    public void setGestioneCorpoDati(GestioneCorpoDati gestioneCorpo) {
        gestioneCorpoDati = gestioneCorpo;
    }

    public void setGestioneLuogo(GestioneLuogo gestioneL) {
        gestioneLuogo = gestioneL;
    }

    public void setGestioneTipoVisita(GestioneTipoVisita gestioneTV) {
        gestioneTipoVisita = gestioneTV;
    }

    public void menuAggiuntaDatiAttivita(CorpoDati dati) {
        int scelta;

        do {
            scelta = viewMenu.menuView(TITOLO_MENU_AGGIUNTA, VOCI_AGGIUNTA_DATI);

            switch (scelta) {
                case SCELTA_VOLONTARIO:
                    gestioneTipoVisita.aggiungiVolontarioAlTipoVisita(dati);
                    break;
                case SCELTA_LUOGO:
                    gestioneCorpoDati.aggiungiLuogo(dati);
                    break;
                case SCELTA_TIPO_VISITA:
                    gestioneLuogo.aggiungiTipoVisita(dati);
                    break;
                case SCELTA_USCITA:
                    viewMenu.showResult(MenuOutcome.USCITA_IN_CORSO);
                    return;
                default:
                    viewMenu.showResult(MenuOutcome.SCELTA_NON_VALIDA);
                    break;
            }
        } while (scelta != 0);
    }

    public void menuRimozioneDatiAttivita(CorpoDati dati) {
        int scelta;

        do {
            scelta = viewMenu.menuView(TITOLO_MENU_RIMOZIONE, VOCI_RIMOZIONE_DATI);

            switch (scelta) {
                case SCELTA_VOLONTARIO:
                    gestioneCorpoDati.rimuoviVolontario(dati);
                    break;
                case SCELTA_LUOGO:
                    gestioneCorpoDati.rimuoviLuogo(dati);
                    break;
                case SCELTA_TIPO_VISITA:
                    gestioneCorpoDati.rimuoviTipoVisita(dati);
                    break;
                case SCELTA_USCITA:
                    outputPrinter.visualizzaMessaggio(MSG_USCITA_MENU_RIMOZIONE);
                    return;
                default:
                    outputPrinter.visualizzaMessaggio(MSG_SCELTA_NON_VALIDA);
                    break;
            }
        } while (scelta != SCELTA_USCITA);
    }
}
