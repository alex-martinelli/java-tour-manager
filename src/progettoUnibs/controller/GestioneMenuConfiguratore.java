package progettoUnibs.controller;

import java.util.List;
import java.util.Map;

import progettoUnibs.model.CorpoDati;
import progettoUnibs.model.TipoUtente;
import progettoUnibs.view.OutputPrinter;
import progettoUnibs.view.OutputVisita;
import progettoUnibs.view.ViewMenu;
import progettoUnibs.view.ViewMenu.MenuOutcome;

public class GestioneMenuConfiguratore {
    
    private static final String OPZIONE_APRI_RACCOLTA_DISPONIBILITA = "Apri raccolta disponibilità volontari";
    private static final String OPZIONE_INSERIRE_DATE_PRECLUSE = "Inserire date precluse";
    private static final String OPZIONE_IMPOSTA_NUMERO_MASSIMO = "Imposta numero massimo di iscritti per prenotazione";
    private static final String OPZIONE_VISUALIZZA_VOLONTARI = "Visualizza elenco volontari";
    private static final String OPZIONE_VISUALIZZA_LUOGHI = "Visualizza luoghi visitabili";
    private static final String OPZIONE_VISUALIZZA_TIPI_VISITA = "Visualizza tipi di visita per ciascun luogo";
    private static final String OPZIONE_VISUALIZZA_STATO_VISITE = "Visualizza le visite in stato di visita proposta/completa/confermata/cancellata/effettuata";
    private static final String OPZIONE_CREA_ATTIVITA = "Crea attività";
    private static final String OPZIONE_AGGIUNTA_DATI = "Aggiunta dati attività";
    private static final String OPZIONE_RIMOZIONE_DATI = "Rimozione dati attività";
    private static final String TITOLO_MENU_CONFIGURATORE = "MENU CONFIGURATORE";
    private static final String MSG_ERRORE_LUOGHI_RIMOSSI = "Sono stati rimossi tutti i luoghi, prima di proseguire aggiungine almeno uno!";
    private static final int VALORE_USCITA = 0;
    private static final int OFFSET_INDICE = 1;

    private ViewMenu viewMenu = new ViewMenu();
    private OutputVisita visitaView = new OutputVisita();
    private OutputPrinter outputPrinter = new OutputPrinter();
    private GestioneVisite gestioneVisite;
    private RepositoryManager repositoryManager;
    private GestioneAttivita gestioneAttivita;
    private GestioneCorpoDati gestioneCorpoDati;
    private GestioneMenuAttivita gestioneMenuAttivita;
    private GestioneDisponibilita gestioneDisponibilita;
    private GestioneMenu gestioneMenu;

    public GestioneMenuConfiguratore(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
        setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
        setGestioneMenu(new GestioneMenu(repositoryManager));
        setGestioneAttivita(new GestioneAttivita(repositoryManager));
        setGestioneMenuAttivita(new GestioneMenuAttivita(repositoryManager));
        setGestioneDisponibilita(new GestioneDisponibilita(repositoryManager));
        setGestioneVisite(new GestioneVisite(repositoryManager));
    }

    public void setGestioneAttivita(GestioneAttivita gestioneAtt) {
        gestioneAttivita = gestioneAtt;
    }

    public void setGestioneCorpoDati(GestioneCorpoDati gestioneCorpo) {
        gestioneCorpoDati = gestioneCorpo;
    }

    public void setGestioneDisponibilita(GestioneDisponibilita gestioneDisp) {
        gestioneDisponibilita = gestioneDisp;
    }

    public void setGestioneMenu(GestioneMenu gestioneM) {
        gestioneMenu = gestioneM;
    }

    public void setGestioneMenuAttivita(GestioneMenuAttivita gestioneMenuAtt) {
        gestioneMenuAttivita = gestioneMenuAtt;
    }

    public void setGestioneVisite(GestioneVisite gestioneVis) {
        gestioneVisite = gestioneVis;
    }

    public void menuConfiguratore(CorpoDati dati) {
        if (dati == null) return;
        dati = repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(dati.getOrganizzazione());
        gestioneVisite.setDati(dati);
        gestioneCorpoDati.setDati(dati);
        while (true) {
            List<String> vociList = gestioneMenu.generaOpzioniMenu(dati);

            String[] voci = vociList.toArray(new String[0]);

            String sceltaTesto;
            int sceltaNum = viewMenu.menuView(TITOLO_MENU_CONFIGURATORE, voci);
            if (sceltaNum == VALORE_USCITA)
                return;

            sceltaTesto = voci[sceltaNum - OFFSET_INDICE];
            switch (sceltaTesto) {
                case OPZIONE_APRI_RACCOLTA_DISPONIBILITA:
                    if (repositoryManager.getCorpoDatiRepository().leggiCorpoDatiDaFile(dati.getOrganizzazione()).getLuoghi().isEmpty()) {
                        outputPrinter.visualizzaMessaggio(MSG_ERRORE_LUOGHI_RIMOSSI);
                        break;
                    }
                    gestioneDisponibilita.apriRaccoltaDisponibilita(dati);
                    break;
                case OPZIONE_INSERIRE_DATE_PRECLUSE:
                    gestioneAttivita.setDati(dati);
                    gestioneAttivita.inserireDatePrecluse();
                    break;
                case OPZIONE_IMPOSTA_NUMERO_MASSIMO:
                    gestioneCorpoDati.modificaNumeroMassimoPersone();
                    break;
                case OPZIONE_VISUALIZZA_VOLONTARI:
                    Map<String, List<String>> volontariMap = repositoryManager.getVolontariRepository().costruisciMappaVolontari(dati);
                    visitaView.visualizzaElencoVolontari(volontariMap);
                    break;
                case OPZIONE_VISUALIZZA_LUOGHI:
                    gestioneCorpoDati.visualizzaLuoghi();
                    break;
                case OPZIONE_VISUALIZZA_TIPI_VISITA:
                    gestioneCorpoDati.visualizzaTipiVisite();
                    break;
                case OPZIONE_VISUALIZZA_STATO_VISITE:
                    gestioneVisite.visualizzaStatoVisite(TipoUtente.CONFIGURATORE);
                    break;
                case OPZIONE_CREA_ATTIVITA:
                    gestioneAttivita.creaGestioneAttivitaDaCorpoDati(dati);
                    break;
                case OPZIONE_AGGIUNTA_DATI:
                    gestioneMenuAttivita.menuAggiuntaDatiAttivita(dati);
                    break;
                case OPZIONE_RIMOZIONE_DATI:
                    gestioneMenuAttivita.menuRimozioneDatiAttivita(dati);
                    break;
                default:
                    viewMenu.showResult(MenuOutcome.SCELTA_NON_VALIDA);
                    break;
            }

        }
    }
}
