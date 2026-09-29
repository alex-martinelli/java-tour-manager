package progettoUnibs.view;

import java.time.LocalTime;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import progettoUnibs.model.TipoVisita;
import progettoUnibs.model.Visita;

public class OutputVisita {
    
    private static final String ELENCO_VOLONTARI = "Elenco volontari e le loro visite:";
    private static final String BIGLIETTO_NO_MINUSCOLO = "no";
    private static final String BIGLIETTO_SI_MINUSCOLO = "si";
    private static final String BIGLIETTO_NO_MAIUSCOLO = "No";
    private static final String BIGLIETTO_SI_MAIUSCOLO = "Si";
    private static final String CONVOCAZIONE_VISITA = "\n\n convocazione per la visita: ";
    private static final String LABEL_TITOLO = "\n titolo:";
    private static final String LABEL_DESCRIZIONE = "\n descrizione: ";
    private static final String LABEL_PUNTO_INCONTRO = "\n punto incontro:";
    private static final String LABEL_DATA = "\n data: ";
    private static final String LABEL_ORA_INIZIO = "\n ora inizio:";
    private static final String LABEL_BIGLIETTO = "\n biglietto necessario:";
    private static final String LABEL_STATO = "\n stato:";
    private static final String SEPARATORE_VISITA = "------------\n ";
    private static final String PREFISSO_VOLONTARIO = "- ";
    private static final String LABEL_VISITE = " (Visite: ";
    private static final String SEPARATORE_VISITE = ", ";
    private static final String CHIUSURA_PARENTESI = ")";
    private static final String VISITA_PROPOSTA = "Visita proposta: ";
    private static final String SEPARATORE_DATA = " - Data: ";
    private static final String POSTI_RIMANENTI = "  Posti rimanenti:";

    public void stampaVisitaView(Visita v) {
                TipoVisita tv = v.getTipoVisita();
                String titolo = v.getTipoVisita().getTitolo();
                String data = v.getData().toString();
                String descrizione = tv.getDescrizione();
                String puntoIncontro = tv.getPuntoIncontro();
                String oraInizio = tv.getOraInizio().toString();
                String biglietto = (tv.getBigliettoNecessario() == false) ? BIGLIETTO_NO_MINUSCOLO : BIGLIETTO_SI_MINUSCOLO;
        System.out.println(
                CONVOCAZIONE_VISITA + LABEL_TITOLO + titolo
                        + LABEL_DESCRIZIONE + descrizione
                        + LABEL_PUNTO_INCONTRO + puntoIncontro + LABEL_DATA + data + LABEL_ORA_INIZIO
                        + oraInizio + LABEL_BIGLIETTO + biglietto);

    }

    public void visualizzaElencoVolontari(Map<String, List<String>> volontariMap) {
        System.out.println(ELENCO_VOLONTARI);
        for (Map.Entry<String, List<String>> entry : volontariMap.entrySet()) {
            System.out.println(PREFISSO_VOLONTARIO + entry.getKey() + LABEL_VISITE + String.join(SEPARATORE_VISITE, entry.getValue()) + CHIUSURA_PARENTESI);
        }
    }

    public void stampaVisita(Visita visita, String stato) {
        TipoVisita tipoVisita = visita.getTipoVisita();
        String titolo = tipoVisita.getTitolo();
        MonthDay data = visita.getData();
        String descrizione = tipoVisita.getDescrizione();
        String puntoIncontro = tipoVisita.getPuntoIncontro();
        LocalTime oraInizio = tipoVisita.getOraInizio();
        String biglietto = (tipoVisita.getBigliettoNecessario() == false) ? BIGLIETTO_NO_MAIUSCOLO
                : BIGLIETTO_SI_MAIUSCOLO;
        System.out.println(SEPARATORE_VISITA + LABEL_TITOLO + titolo + LABEL_DESCRIZIONE + descrizione
                + LABEL_PUNTO_INCONTRO + puntoIncontro + LABEL_DATA + data + LABEL_ORA_INIZIO
                + oraInizio + LABEL_BIGLIETTO + biglietto + LABEL_STATO + stato);
    }

    public void visualizzaVisite(ArrayList<Visita> visite) {
        for (Visita visita : visite) {
            System.out.println(VISITA_PROPOSTA + visita.getTipoVisita().getTitolo() + SEPARATORE_DATA + visita.getData()
                    + POSTI_RIMANENTI
                    + (visita.getTipoVisita().getMassimoPartecipanti() - visita.getNumeroIscritti()));
        }
    }
}
