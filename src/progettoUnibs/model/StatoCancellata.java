package progettoUnibs.model;

import java.time.LocalDate;
import java.util.Iterator;

public class StatoCancellata implements StatoVisita {

    private static final String NOME_STATO = "CANCELLATA";

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
        if (context.getData().atYear(LocalDate.now().getYear()).isBefore(LocalDate.now())) {
            if (context.getStato() instanceof StatoCancellata) {
                iterator.remove();
            }
        }
    }

    @Override
    public String getNome() {
        return NOME_STATO;
    }
}
