package progettoUnibs.controller;

import java.util.*;


import progettoUnibs.model.TipoVisita;

import progettoUnibs.view.InputReader;
import progettoUnibs.view.OutputPrinter;

public class GestioneVolontari {
    
    private static final String INSERISCI_NICKNAME_NUOVO_VOLONTARIO = "Inserisci il nickname del nuovo volontario: ";
    private static final String VOLONTARIO_AGGIUNTO_ALLA_LISTA = "Volontario aggiunto alla lista esistente";
    private static final String VOLONTARIO_GIA_PRESENTE = "Volontario già presente.";
    private static final String INSERISCI_NICKNAME_VOLONTARIO = "Inserisci il nickname del volontario: ";
    private static final String VOLONTARIO_AGGIUNTO = "Volontario aggiunto ";
    private static final String VOLONTARIO_NON_DISPONIBILE = "Volontario non disponibile!";
    private static final String MSG_NICKNAME_GIA_IN_USO = "Il nickname è già in uso.";

    private Set<String> listaVolontari;
    private OutputPrinter outputPrinter = new OutputPrinter();
    private RepositoryManager repositoryManager;
    private GestioneVolontario gestioneVolontario;

    public GestioneVolontari(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    public void setGestioneVolontario(GestioneVolontario gestioneVol) {
        gestioneVolontario = gestioneVol;
    }

    public boolean esisteNickname(String nickname) {
        return listaVolontari.contains(nickname);
    }

    public void aggiungiNuovoVolontario(ArrayList<String> volontari, String organizzazione) {
        listaVolontari = repositoryManager.getVolontariRepository().leggiVolontariDaTxt(organizzazione);
        String nickname = InputReader.leggiStringaNonVuota(INSERISCI_NICKNAME_NUOVO_VOLONTARIO);
        if (!repositoryManager.getCredenzialiRepository().esisteUsernameGlobale(nickname)) {
            volontari.add(nickname);
            listaVolontari.add(nickname);
            repositoryManager.getVolontariRepository().scriviListaVolontari(organizzazione, nickname);
            outputPrinter.visualizzaMessaggio(VOLONTARIO_AGGIUNTO_ALLA_LISTA);
        } else {
            outputPrinter.visualizzaMessaggio(MSG_NICKNAME_GIA_IN_USO);
        }

    }

    public void aggiungiVolontarioEsistente(ArrayList<String> volontari, String organizzazione, TipoVisita tipo) {
        listaVolontari = repositoryManager.getVolontariRepository().leggiVolontariDaTxt(organizzazione);
        String nickname = InputReader.leggiStringaNonVuota(INSERISCI_NICKNAME_VOLONTARIO);
        setGestioneVolontario(new GestioneVolontario(repositoryManager));
        if (esisteNickname(nickname) && !gestioneVolontario.volontarioOccupato(nickname, organizzazione, tipo)) {
            if (volontari.contains(nickname)) {
                outputPrinter.visualizzaMessaggio(VOLONTARIO_GIA_PRESENTE);
            } else {
                volontari.add(nickname);
                outputPrinter.visualizzaMessaggio(VOLONTARIO_AGGIUNTO);
            }
        } else {
            outputPrinter.visualizzaMessaggio(VOLONTARIO_NON_DISPONIBILE);
        }
    }

    

    public boolean volontariAssegnati(String organizzazione) {
        Set<String> volontariTxt = repositoryManager.getVolontariRepository().leggiVolontariDaTxt(organizzazione);
        Set<String> volontariJson = repositoryManager.getVolontariRepository().leggiVolontariDaJson(organizzazione);

        boolean tuttiPresenti = volontariJson.containsAll(volontariTxt);

        return tuttiPresenti;
    }
}
