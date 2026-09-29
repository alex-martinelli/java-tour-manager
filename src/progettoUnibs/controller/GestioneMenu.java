package progettoUnibs.controller;

import java.time.LocalDate;

import java.util.List;

import progettoUnibs.model.CorpoDati;

import progettoUnibs.model.Menu;
import progettoUnibs.model.TipoUtente;

import progettoUnibs.view.*;

public class GestioneMenu {

    private static final String[] VOCIAVVIO = { "Configuratore", "Volontario", "Fruitore" };
    private static final String TITOLO_MENU_AVVIO = "MENU DI AVVIO";
    private static final int SCELTA_FRUITORE = 3;
    private static final int SCELTA_VOLONTARIO = 2;
    private static final int SCELTA_CONFIGURATORE = 1;
    private static final int SCELTA_USCITA = 0;
    private static final int GIORNO_LIMITE_VISITE = 15;
    private static final int MESI_OFFSET_RACCOLTA = 2;
    private static final int MESI_OFFSET_MODIFICA = 1;
    private static final int DIMENSIONE_VISITE_MINIMA = 0;
    
    private ViewMenu viewMenu = new ViewMenu();
    private RepositoryManager repositoryManager;
    private GestioneLogin gestioneLogin;
    private GestioneMenuFruitore gestioneMenuFruitore;

    public GestioneMenu(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void setGestioneLogin(GestioneLogin gestioneLog) {
        gestioneLogin = gestioneLog;
    }

    public void setGestioneMenuFruitore(GestioneMenuFruitore gestioneMenuF) {
        gestioneMenuFruitore = gestioneMenuF;
    }

    public void menuAvvio() {
        int scelta;
        do {
            scelta = viewMenu.menuView(TITOLO_MENU_AVVIO, VOCIAVVIO);
            switch (scelta) {
                case SCELTA_FRUITORE:
                    gestioneMenuFruitore.menuAccessoFruitore();
                    break;
                case SCELTA_VOLONTARIO:
                    gestioneLogin.login(TipoUtente.VOLONTARIO);
                    break;
                case SCELTA_CONFIGURATORE:
                    gestioneLogin.login(TipoUtente.CONFIGURATORE);
                    break;
                case SCELTA_USCITA:
                    viewMenu.showResult(ViewMenu.MenuOutcome.USCITA_IN_CORSO);
                    break;
                default:
                    viewMenu.showResult(ViewMenu.MenuOutcome.SCELTA_NON_VALIDA);
                    break;
            }
        } while (scelta != SCELTA_USCITA);
    }

    public List<String> generaOpzioniMenu(CorpoDati dati) {
        int dimensioneVisite = LocalDate.now().getDayOfMonth() <= GIORNO_LIMITE_VISITE ? repositoryManager.getAttivitaRepository()
                .leggiAttivitaDaFile(dati.getOrganizzazione(), LocalDate.now().getMonth()).getVisite().size()
                : repositoryManager.getAttivitaRepository()
                        .leggiAttivitaDaFile(dati.getOrganizzazione(), LocalDate.now().getMonth().plus(MESI_OFFSET_MODIFICA))
                        .getVisite().size();
        boolean raccoltaAperta = repositoryManager.getDisponibilitaRepository().leggiDisponibilita(
                LocalDate.now().plusMonths(MESI_OFFSET_RACCOLTA).getMonth(), dati.getOrganizzazione()).isRaccoltaAperta();
        boolean condModificaAttivita = (repositoryManager.getAttivitaRepository()
                .leggiAttivitaDaFile(dati.getOrganizzazione(), LocalDate.now().plusMonths(MESI_OFFSET_MODIFICA).getMonth())
                .getVisite().size() > DIMENSIONE_VISITE_MINIMA) || (LocalDate.now().getDayOfMonth() <= GIORNO_LIMITE_VISITE);

        List<String> vociList = Menu.creaListaVoci(dimensioneVisite, dati, raccoltaAperta, condModificaAttivita);
        return vociList;
    }

}
