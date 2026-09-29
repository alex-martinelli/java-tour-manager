package progettoUnibs.model;

import java.time.MonthDay;
import java.util.Iterator;

public class StatoConfermata implements StatoVisita {

    private static final String NOME_STATO = "CONFERMATA";

    @Override
    public void aggiornaStatoVis(Visita context, Visita visita) {
       
    }

    @Override
    public void aggiornaStatoNuovaPrenotazione(Visita context) {
    
    }

    @Override
    public void checkStato(Visita context) {
        if (context.getData().isBefore(MonthDay.now())) {
            context.setStato(new StatoEffettuata());
        }

    }

    @Override
    public void rimuoviVisitaCancellata(Visita context, Iterator<Visita> iterator) {
     
    }

    @Override
    public String getNome() {
        return NOME_STATO;
    }
}
