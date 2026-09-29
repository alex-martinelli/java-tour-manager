package progettoUnibs.model;

import java.util.Iterator;

public class StatoEffettuata implements StatoVisita {

    private static final String NOME_STATO = "EFFETTUATA";

    @Override
    public void aggiornaStatoVis(Visita context, Visita visita) {

    }

    @Override
    public void aggiornaStatoNuovaPrenotazione(Visita context) {
        
    }

    @Override
    public void checkStato(Visita context) {
    
    }

    @Override
    public void rimuoviVisitaCancellata(Visita context, Iterator<Visita> iterator) {
      
    }

    @Override
    public String getNome() {
        return NOME_STATO;
    }
}
