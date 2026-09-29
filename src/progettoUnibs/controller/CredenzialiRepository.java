package progettoUnibs.controller;

import java.util.Map;

import progettoUnibs.model.TipoUtente;

public interface CredenzialiRepository {
    Map<String, String> caricaCredenzialiDaFile(String filePath);
    Map<String, String> caricaCredenzialiConfDaFile(String org);
    Map<String, String> caricaCredenzialiVolDaFile(String org);
    boolean checkOrganizzazione(String organizzazione);
    void modificaCredenziali(String org, String username, String newUsername, String newPassword, TipoUtente tipo);
    void registraFruitore(String username, String password);
    boolean checkFruitore(String username, String password);
    boolean esisteUsernameGlobale(String username);
}
