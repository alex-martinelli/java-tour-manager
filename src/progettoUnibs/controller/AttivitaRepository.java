package progettoUnibs.controller;

import java.time.Month;

import progettoUnibs.model.Attivita;
import progettoUnibs.model.Prenotazione;
import progettoUnibs.model.Visita;

public interface AttivitaRepository {
    public Attivita leggiAttivitaDaFile(String organizzazione, Month mese);
    public void salvaFileAttivita(String organizzazione, Attivita g);
    public void checkStatoVisite();
    public void aggiornaVisitaInAttivita(Prenotazione prenotazione, Visita visita, String org);
    public void rimuoviPrenotazioneDaVisita(int codicePrenotazione, String fruitore, String org, Visita visita);
}
