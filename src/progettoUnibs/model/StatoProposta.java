package progettoUnibs.model;

import java.time.LocalDate;
import java.time.Year;
import java.util.Iterator;

public class StatoProposta implements StatoVisita {

    private static final String NOME_STATO = "PROPOSTA";
    private static final int GIORNI_LIMITE_CONFERMA = 3;

    @Override
    public void aggiornaStatoVis(Visita context, Visita visita) {
    }

    @Override
    public void aggiornaStatoNuovaPrenotazione(Visita context) {
        if (context.getNumeroIscritti() == context.getTipoVisita().getMassimoPartecipanti()) {
            context.setStato(new StatoCompleta());
        }
    }

    @Override
    public void checkStato(Visita context) {
        LocalDate dataOdierna = context.getData().atYear(Year.now().getValue());
        LocalDate dataLimite = LocalDate.now().plusDays(GIORNI_LIMITE_CONFERMA);

        if (dataOdierna.isBefore(dataLimite)) {
            if (context.getNumeroIscritti() >= context.getTipoVisita().getMinimoPartecipanti()) {
                context.setStato(new StatoConfermata());
            } else {
                context.setStato(new StatoCancellata());
            }
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
