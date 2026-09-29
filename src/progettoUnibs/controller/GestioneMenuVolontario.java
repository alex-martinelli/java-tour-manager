package progettoUnibs.controller;

import java.util.ArrayList;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Volontario;
import progettoUnibs.view.InputReader;
import progettoUnibs.view.ViewMenu;
import progettoUnibs.view.ViewMenu.MenuOutcome;

public class GestioneMenuVolontario {
    private static final String CONFIGURATORE_COSA_VUOI_FARE = "Configuratore, cosa vuoi fare?";
    private static final String AGGIUNGI_NUOVO_VOLONTARIO = "Aggiungi nuovo volontario";
    private static final String AGGIUNGI_VOLONTARIO_ESISTENTE = "Aggiungi volontario esistente";
    private static final String VUOI_INSERIRE_ALTRI_VOLONTARI = "Vuoi inserire altri volontari?";
    private static final String[] VOCIVOLONTARIO = {
            "Visualizza tipi di visita associati",
            "Inserisci disponibilità", "Visualizza convocazioni",
            "visualizza prenotazioni relative a una visita confermata"
    };
    private static final String[] VOCI_AGGIUNTA_VOLONTARI = { AGGIUNGI_NUOVO_VOLONTARIO, AGGIUNGI_VOLONTARIO_ESISTENTE };
    
    private static final String TITOLO_MENU_VOLONTARIO = "MENU VOLONTARIO";
    
    private static final int SCELTA_VISUALIZZA_TIPI_VISITA = 1;
    private static final int SCELTA_INSERISCI_DISPONIBILITA = 2;
    private static final int SCELTA_VISUALIZZA_CONVOCAZIONI = 3;
    private static final int SCELTA_VISUALIZZA_PRENOTAZIONI = 4;
    private static final int SCELTA_USCITA = 0;
    private static final int SCELTA_NUOVO_VOLONTARIO = 1;
    private static final int SCELTA_VOLONTARIO_ESISTENTE = 2;

    private ViewMenu viewMenu = new ViewMenu();
    private GestioneVisite gestioneVisite;
    private GestioneDisponibilita gestioneDisponibilita;
    private GestioneVolontario gestioneVolontario;
    private GestioneVolontari gestioneVolontari;
    private RepositoryManager repositoryManager;

    public GestioneMenuVolontario(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneDisponibilita(new GestioneDisponibilita(repositoryManager));
        setGestioneVolontario(new GestioneVolontario(repositoryManager));
        setGestioneVolontari(new GestioneVolontari(repositoryManager));
        setGestioneVisite(new GestioneVisite(repositoryManager));
    }

    public void setGestioneVolontari(GestioneVolontari gestioneVol) {
        gestioneVolontari = gestioneVol;
    }

    public void setGestioneDisponibilita(GestioneDisponibilita gestioneDisp) {
        gestioneDisponibilita = gestioneDisp;
    }

    public void setGestioneVolontario(GestioneVolontario gestioneVol) {
        gestioneVolontario = gestioneVol;
    }

    public void setGestioneVisite(GestioneVisite gestioneVis) {
        gestioneVisite = gestioneVis;
    }

    public void menuVolontario(CorpoDati dati, Volontario volontario) {

        if(dati == null){
            viewMenu.showResult(ViewMenu.MenuOutcome.DATI_NON_PRESENTI);
            return;
        }
        dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(dati.getOrganizzazione());
        gestioneVisite.setDati(dati);

        int scelta;
        do {
            scelta = viewMenu.menuView(TITOLO_MENU_VOLONTARIO, VOCIVOLONTARIO);
            switch (scelta) {
                case SCELTA_VISUALIZZA_PRENOTAZIONI:
                    gestioneVolontario.visualizzaPrenotazioniVisita(volontario);
                    break;
                case SCELTA_VISUALIZZA_CONVOCAZIONI:
                    gestioneVolontario.visualizzaConvocazioni(volontario);
                    break;
                case SCELTA_INSERISCI_DISPONIBILITA:
                    gestioneDisponibilita.aggiungiDisponibilita(volontario);
                    break;
                case SCELTA_VISUALIZZA_TIPI_VISITA:
                    gestioneVolontario.visualizzaVisitePerVolontario(volontario.getCredenziali().getUsername(), dati);
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

    public ArrayList<String> aggiungiVolontariMenu(String organizzazione, TipoVisita tipo) {
        ArrayList<String> volontari = new ArrayList<>();
        boolean almenoUnoAggiunto = false;

        do {
            int scelta = viewMenu.menuView(CONFIGURATORE_COSA_VUOI_FARE, VOCI_AGGIUNTA_VOLONTARI);

            if (scelta == SCELTA_NUOVO_VOLONTARIO) {
                gestioneVolontari.aggiungiNuovoVolontario(volontari, organizzazione);
            } else if (scelta == SCELTA_VOLONTARIO_ESISTENTE) {
                gestioneVolontari.aggiungiVolontarioEsistente(volontari, organizzazione, tipo);
            }
            if (!volontari.isEmpty()) {
                almenoUnoAggiunto = true;
            }

            if (almenoUnoAggiunto) {
                if (InputReader.yesOrNo(VUOI_INSERIRE_ALTRI_VOLONTARI)) {
                    almenoUnoAggiunto = false;
                }
            }
        } while (!almenoUnoAggiunto);

        return volontari;
    }

}
