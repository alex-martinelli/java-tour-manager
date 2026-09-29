package progettoUnibs.model;

import java.time.LocalDate;
import java.time.Year;
import java.util.Iterator;

public class StatoCompleta implements StatoVisita {

    private static final String NOME_STATO = "COMPLETA";
    private static final int GIORNI_LIMITE_CONFERMA = 3;

    @Override
    public void aggiornaStatoVis(Visita context, Visita visita) {
        if (visita.getData().equals(context.getData())) {
            context.setStato(new StatoProposta());
        }
    }

    @Override
    public void aggiornaStatoNuovaPrenotazione(Visita context) {
        
    }

    @Override
    public void checkStato(Visita context) {
        LocalDate dataOdierna = context.getData().atYear(Year.now().getValue());
        LocalDate dataLimite = LocalDate.now().plusDays(GIORNI_LIMITE_CONFERMA);

        if (dataOdierna.isBefore(dataLimite)) {
            context.setStato(new StatoConfermata());
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
