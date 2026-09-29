package progettoUnibs.controller;

import java.util.ArrayList;

import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.Visita;

public interface PrenotazioniRepository {
    Prenotazione eseguiIscrizione(Visita visita, String org, String fruitore, int numeroIscrizioni);
    void aggiungiPrenotazioneSuFile(Prenotazione nuovaPrenotazione, String org);
    ArrayList<Prenotazione> leggiPrenotazioniDaFile(String org);
    void rimuoviPrenotazioneDaFile(int codicePrenotazione, String fruitore, String org);
}
