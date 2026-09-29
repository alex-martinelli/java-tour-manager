package progettoUnibs.model;

import java.util.Iterator;

public interface StatoVisita {
    void aggiornaStatoVis(Visita context, Visita visita);
    void aggiornaStatoNuovaPrenotazione(Visita context);
    void checkStato(Visita context);
    void rimuoviVisitaCancellata(Visita context, Iterator<Visita> iterator);
    String getNome();
}
