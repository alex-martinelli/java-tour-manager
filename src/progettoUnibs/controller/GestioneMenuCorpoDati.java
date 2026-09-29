package progettoUnibs.controller;

import java.util.ArrayList;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.Luogo;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.ViewMenu;

public class GestioneMenuCorpoDati {
    private static final String CONFIGURATORE_COSA_VUOI_FARE = "Configuratore, cosa vuoi fare?";
    private static final String AGGIUNGI_LUOGO = "Aggiungi Luogo";
    private static final String PROSEGUI = "Prosegui";
    private static final String DEVI_AGGIUNGERE_ALMENO_UN_LUOGO_USCIRE = "Devi aggiungere almeno un luogo prima di uscire!";
    private static final String SCELTA_NON_VALIDA = "Scelta non valida, riprova.";
    
    private static final int SCELTA_AGGIUNGI_LUOGO = 1;
    private static final int SCELTA_PROSEGUI = 2;
    private static final int SCELTA_USCITA = 0;
   
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneLuogo gestioneLuogo;
    private GestioneCorpoDati gestioneCorpoDati;
    private RepositoryManager repositoryManager;

    public GestioneMenuCorpoDati(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneLuogo(new GestioneLuogo(repositoryManager));
    }

    public void setGestioneLuogo(GestioneLuogo gestioneL) {
        gestioneLuogo = gestioneL;
    }

    public void setGestioneCorpoDati(GestioneCorpoDati gestioneCorpo) {
        gestioneCorpoDati = gestioneCorpo;
    }

    public void menuCreazioneDati(CorpoDati corpo, boolean continua, ArrayList<Luogo> luoghi,
            String organizzazione) {
        String[] voci = { AGGIUNGI_LUOGO, PROSEGUI };
        ViewMenu menu = new ViewMenu();
        int scelta = menu.scegli(CONFIGURATORE_COSA_VUOI_FARE, voci);
        setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));

        switch (scelta) {
            case SCELTA_AGGIUNGI_LUOGO:
                gestioneLuogo.creazioneLuogo(corpo, luoghi);
                break;

            case SCELTA_PROSEGUI:
                continua = gestioneCorpoDati.proseguiCorpoDati(organizzazione, corpo, luoghi, continua);
                break;

            case SCELTA_USCITA:
                if (luoghi.isEmpty()) {
                    outputPrinter.visualizzaMessaggio(DEVI_AGGIUNGERE_ALMENO_UN_LUOGO_USCIRE);
                } else {
                    return;
                }
                break;

            default:
                outputPrinter.visualizzaMessaggio(SCELTA_NON_VALIDA);
        }
    }

}
