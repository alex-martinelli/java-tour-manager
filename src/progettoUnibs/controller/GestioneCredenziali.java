package progettoUnibs.controller;


import java.util.*;


import progettoUnibs.model.TipoUtente;

import progettoUnibs.view.ViewCredenziali;
import progettoUnibs.view.ViewCredenziali.*;

public class GestioneCredenziali {

    private RepositoryManager repositoryManager;
    private ViewCredenziali viewCredenziali = new ViewCredenziali();
    private Map<String, String> credenzialiCache = new HashMap<>();

    public GestioneCredenziali(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void caricaCredenziali(String org, TipoUtente tipoUtente) {
        if (tipoUtente == TipoUtente.CONFIGURATORE)
            credenzialiCache = repositoryManager.getCredenzialiRepository().caricaCredenzialiConfDaFile(org);
        else {
            credenzialiCache = repositoryManager.getCredenzialiRepository().caricaCredenzialiVolDaFile(org);
        }
    }


    public void cambiaCredenziali(String organizzazione, String oldUsername, TipoUtente tipoUtente) {

        String newUsername = oldUsername;
        if (tipoUtente == TipoUtente.CONFIGURATORE) {
            do {
                viewCredenziali.showResult(LoginOutcome.DEVI_CAMBIARE_USERNAME);
                newUsername = viewCredenziali.askUsername();
            } while (repositoryManager.getCredenzialiRepository().esisteUsernameGlobale(newUsername));
        }
        viewCredenziali.showResult(LoginOutcome.DEVI_CAMBIARE_PASSWORD);
        String newPassword = viewCredenziali.askPassword();
        repositoryManager.getCredenzialiRepository().modificaCredenziali(organizzazione, oldUsername, newUsername, newPassword, tipoUtente);
        viewCredenziali.showResult(LoginOutcome.CREDENZIALI_AGGIORNATE);
    }

    public boolean controllaUsername(String username) {
        if (repositoryManager.getCredenzialiRepository().esisteUsernameGlobale(username)) {
            viewCredenziali.showResult(LoginOutcome.USERNAME_EXISTS);
            return true;
        }
        return false;
    }

}