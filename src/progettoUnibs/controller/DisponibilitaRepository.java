package progettoUnibs.controller;

import java.time.Month;

import progettoUnibs.model.Disponibilita;

public interface DisponibilitaRepository {
    Disponibilita leggiDisponibilita(Month mese, String organizzazione);
    void salvaDisponibilita(Disponibilita disponibilita, String organizzazione);
    void cancellaFileDisponibilita(String organizzazione, Month meseSuccessivo);
}
