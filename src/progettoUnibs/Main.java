package progettoUnibs;

import progettoUnibs.controller.*;

public class Main {
    public static void main(String[] args) {
        RepositoryManager repositoryManager = new RepositoryManager(
                new AttivitaFileManager(),
                new CorpoDatiFileManager(),
                new DisponibilitaFileManager(),
                new CredenzialiFileManager(),
                new VolontariFileManager(),
                new PrenotazioniFileManager());
        GestioneMenu gestioneMenu = new GestioneMenu(repositoryManager);
        GestioneMenuFruitore gestioneMenuFruitore = new GestioneMenuFruitore(repositoryManager);
        GestioneLogin gestioneLogin = new GestioneLogin(repositoryManager, gestioneMenuFruitore);
        GestioneMenuCorpoDati gestioneMenuCorpoDati = new GestioneMenuCorpoDati(repositoryManager);

        gestioneMenuCorpoDati.setGestioneCorpoDati(new GestioneCorpoDati(repositoryManager));
        gestioneMenu.setGestioneLogin(gestioneLogin);
        gestioneMenu.setGestioneMenuFruitore(gestioneMenuFruitore);


        repositoryManager.getAttivitaRepository().checkStatoVisite();
        gestioneMenu.menuAvvio();
    }
}
